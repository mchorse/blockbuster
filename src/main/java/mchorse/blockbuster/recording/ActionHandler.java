package mchorse.blockbuster.recording;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.render.tileentity.TileEntityModelItemStackRenderer;
import mchorse.blockbuster.client.render.tileentity.TileEntityModelItemStackRenderer.TEModel;
import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketCaption;
import mchorse.blockbuster.network.common.recording.PacketPlayerRecording;
import mchorse.blockbuster.recording.RecordManager.ScheduledRecording;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.AttackAction;
import mchorse.blockbuster.recording.actions.BreakBlockAction;
import mchorse.blockbuster.recording.actions.ChatAction;
import mchorse.blockbuster.recording.actions.CommandAction;
import mchorse.blockbuster.recording.actions.DropAction;
import mchorse.blockbuster.recording.actions.InteractBlockAction;
import mchorse.blockbuster.recording.actions.ItemUseAction;
import mchorse.blockbuster.recording.actions.ItemUseBlockAction;
import mchorse.blockbuster.recording.actions.MorphAction;
import mchorse.blockbuster.recording.actions.MorphActionAction;
import mchorse.blockbuster.recording.actions.MountingAction;
import mchorse.blockbuster.recording.actions.PlaceBlockAction;
import mchorse.blockbuster.recording.actions.ShootArrowAction;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.recording.sounds.SoundEventListener;
import mchorse.metamorph.api.events.MorphActionEvent;
import mchorse.metamorph.api.events.MorphEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Event handler for recording purposes.
 *
 * This event handler listens to different events and then writes them to
 * the recording event list (which in turn are being written to the disk
 * by RecordThread).
 *
 * Taken from Mocap mod and rewritten.
 */
public class ActionHandler
{
    /**
     * Last TE was spotted during block breaking action (used for 
     * damage control of tile entities) 
     */
    public static TileEntity lastTE;

    /**
     * Adds a world event listener
     */
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        World world = event.getWorld();

        if (!world.isRemote)
        {
            SoundEventListener listener = new SoundEventListener(world);

            world.addEventListener(new WorldEventListener(world));
            world.addEventListener(listener);
            MinecraftForge.EVENT_BUS.register(listener);

            SoundEventListener.INSTANCE = listener;
        }

        if (world instanceof WorldServer && ((WorldServer) world).provider.getDimension() == 0)
        {
            Blockbuster.reloadServerModels(true);
        }
    }

    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent.RightClickItem event)
    {
        EntityPlayer player = event.getEntityPlayer();
        List<Action> events = CommonProxy.manager.getActions(player);

        if (!player.world.isRemote && events != null)
        {
            events.add(new ItemUseAction(event.getHand()));
        }
    }

    @SubscribeEvent
    public void onItemUseBlock(PlayerInteractEvent.RightClickBlock event)
    {
        EntityPlayer player = event.getEntityPlayer();
        List<Action> events = CommonProxy.manager.getActions(player);

        if (!player.world.isRemote && events != null)
        {
            Vec3d hit = event.getHitVec();
            BlockPos pos = event.getPos();

            events.add(new ItemUseBlockAction(pos, event.getHand(), event.getFace(), (float) hit.x - pos.getX(), (float) hit.y - pos.getY(), (float) hit.z - pos.getZ()));
        }
    }

    /**
     * Event listener for Action.BREAK_BLOCK
     */
    @SubscribeEvent
    public void onPlayerBreaksBlock(BreakEvent event)
    {
        EntityPlayer player = event.getPlayer();
        List<Action> events = CommonProxy.manager.getActions(player);

        if (Blockbuster.proxy.config.damage_control)
        {
            lastTE = player.world.getTileEntity(event.getPos());
        }

        if (!player.world.isRemote && events != null && player.isCreative())
        {
            events.add(new BreakBlockAction(event.getPos(), false));
        }
    }

    /**
     * Event listener for Action.INTERACT_BLOCK (when player right clicks on
     * a block)
     */
    @SubscribeEvent
    public void onPlayerRightClickBlock(RightClickBlock event)
    {
        EntityPlayer player = event.getEntityPlayer();
        List<Action> events = CommonProxy.manager.getActions(player);

        if (!player.world.isRemote && events != null)
        {
            events.add(new InteractBlockAction(event.getPos()));
        }
    }

    /**
     * Event listener for Action.PLACE_BLOCK
     */
    @SubscribeEvent
    public void onPlayerPlacesBlock(PlaceEvent event)
    {
        EntityPlayer player = event.getPlayer();
        List<Action> events = CommonProxy.manager.getActions(player);

        if (!player.world.isRemote && events != null)
        {
            IBlockState state = event.getPlacedBlock();
            Block block = state.getBlock();

            this.placeBlock(events, event.getPos(), block, state);
        }
    }

    /**
     * Another event listener for Action.PLACE_BLOCK
     */
    @SubscribeEvent
    public void onPlayerPlacesMultiBlock(MultiPlaceEvent event)
    {
        EntityPlayer player = event.getPlayer();
        List<Action> events = CommonProxy.manager.getActions(player);

        if (!player.world.isRemote && events != null)
        {
            List<BlockSnapshot> blocks = event.getReplacedBlockSnapshots();

            for (BlockSnapshot snapshot : blocks)
            {
                IBlockState state = snapshot.getCurrentBlock();
                Block block = state.getBlock();

                this.placeBlock(events, snapshot.getPos(), block, state);
            }
        }
    }

    /**
     * Event listener for Action.ATTACK
     */
    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        List<Action> events = CommonProxy.manager.getActions(player);

        if (!player.world.isRemote && events != null && !Blockbuster.proxy.config.record_attack_on_swipe)
        {
            events.add(new AttackAction());
        }
    }

    /**
     * Event listener for bucket using. When you place water or lava with
     * bucket it doesn't considered place block action like with any other
     * types of blocks.
     *
     * So here's my hack for placing water and lava blocks.
     */
    @SubscribeEvent
    public void onPlayerUseBucket(FillBucketEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        List<Action> events = CommonProxy.manager.getActions(player);
        RayTraceResult target = event.getTarget();

        if (!player.world.isRemote && events != null && target != null && target.typeOfHit == Type.BLOCK)
        {
            Item bucket = event.getEmptyBucket().getItem();
            BlockPos pos = target.getBlockPos().offset(target.sideHit);

            if (bucket == Items.LAVA_BUCKET)
            {
                this.placeBlock(events, pos, Blocks.FLOWING_LAVA, 0);
            }
            else if (bucket == Items.WATER_BUCKET)
            {
                this.placeBlock(events, pos, Blocks.FLOWING_WATER, 0);
            }
        }
    }

    private void placeBlock(List<Action> events, BlockPos pos, Block block, IBlockState state)
    {
        this.placeBlock(events, pos, block, block.getMetaFromState(state));
    }

    /**
     * Place block in given event list
     */
    private void placeBlock(List<Action> events, BlockPos pos, Block block, int metadata)
    {
        ResourceLocation id = block.getRegistryName();

        events.add(new PlaceBlockAction(pos, (byte) metadata, id.toString()));
    }

    /**
     * Event listener for Action.MOUNTING (when player mounts other entity)
     */
    @SubscribeEvent
    public void onPlayerMountsSomething(EntityMountEvent event)
    {
        if (event.getEntityMounting() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.getEntityMounting();
            List<Action> events = CommonProxy.manager.getActions(player);

            if (!player.world.isRemote && events != null)
            {
                events.add(new MountingAction(event.getEntityBeingMounted().getUniqueID(), event.isMounting()));
            }
        }
    }

    /**
     * Event listener for Action.SHOOT_ARROW
     */
    @SubscribeEvent
    public void onArrowLooseEvent(ArrowLooseEvent event) throws IOException
    {
        EntityPlayer player = event.getEntityPlayer();
        List<Action> events = CommonProxy.manager.getActions(player);

        if (!player.world.isRemote && events != null)
        {
            events.add(new ShootArrowAction(event.getCharge()));
        }
    }

    /**
     * Event listener for Action.DROP (when player drops the item from his
     * inventory)
     */
    @SubscribeEvent
    public void onItemTossEvent(ItemTossEvent event) throws IOException
    {
        EntityPlayer player = event.getPlayer();
        List<Action> events = CommonProxy.manager.getActions(player);

        if (!player.world.isRemote && events != null)
        {
            events.add(new DropAction(event.getEntityItem().getItem()));
        }
    }

    /**
     * Event listener for Action.CHAT (basically when the player enters
     * something in the chat)
     */
    @SubscribeEvent
    public void onServerChatEvent(ServerChatEvent event)
    {
        EntityPlayer player = event.getPlayer();
        List<Action> events = CommonProxy.manager.getActions(player);

        if (!player.world.isRemote && events != null)
        {
            events.add(new ChatAction(event.getMessage()));
        }
    }

    /**
     * Event listener for Action.COMMAND (basically when the player enters
     * a command in the chat). Adds an action only for server commands.
     */
    @SubscribeEvent
    public void onPlayerCommand(CommandEvent event)
    {
        if (!Blockbuster.proxy.config.record_commands)
        {
            return;
        }

        ICommandSender sender = event.getSender();

        if (sender instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) sender;
            List<Action> events = CommonProxy.manager.getActions(player);

            if (!player.world.isRemote && events != null)
            {
                String command = "/" + event.getCommand().getName();

                for (String value : event.getParameters())
                {
                    command += " " + value;
                }

                events.add(new CommandAction(command));
            }
        }
    }

    /**
     * Event listener when player logs out. This listener aborts the recording
     * for given player (well, if he records, but that {@link RecordManager}'s
     * job to find out).
     */
    @SubscribeEvent
    public void onPlayerLogOut(PlayerLoggedOutEvent event)
    {
        EntityPlayer player = event.player;

        if (!player.world.isRemote)
        {
            CommonProxy.manager.abortRecording(player);
        }
    }

    /**
     * Event listener for MORPH
     *
     * This is a new event listener for morphing. Before that, there was server
     * handler which was responsible for recoring MORPH action.
     */
    @SubscribeEvent
    public void onPlayerMorph(MorphEvent.Post event)
    {
        EntityPlayer player = event.player;
        List<Action> events = CommonProxy.manager.getActions(player);

        if (!player.world.isRemote && events != null)
        {
            events.add(new MorphAction(event.morph));
        }
    }

    /**
     * Event listener for MORPH_ACTION
     *
     * This method will simply submit a {@link MorphActionAction} to the
     * event list, if action is valid.
     */
    @SubscribeEvent
    public void onPlayerMorphAction(MorphActionEvent event)
    {
        EntityPlayer player = event.player;
        List<Action> events = CommonProxy.manager.getActions(player);

        if (!player.world.isRemote && events != null && event.isValid())
        {
            events.add(new MorphActionAction());
        }
    }

    /**
     * Event listener for world tick event.
     *
     * This is probably not the optimal solution, but I'm not really sure how
     * to schedule things in Minecraft other way than timers and ticks.
     *
     * This method is responsible for scheduling record unloading and counting
     * down recording process.
     */
    @SubscribeEvent
    public void onWorldTick(ServerTickEvent event)
    {
        if (!CommonProxy.manager.records.isEmpty() && Blockbuster.proxy.config.record_unload)
        {
            this.checkAndUnloadRecords();
        }

        if (!CommonProxy.manager.scheduled.isEmpty())
        {
            this.checkScheduled();
        }
    }

    /**
     * Check for any unloaded record and unload it if needed requirements are
     * met.
     */
    private void checkAndUnloadRecords()
    {
        Iterator<Map.Entry<String, Record>> iterator = CommonProxy.manager.records.entrySet().iterator();

        while (iterator.hasNext())
        {
            Record record = iterator.next().getValue();

            record.unload--;

            if (record.unload <= 0)
            {
                iterator.remove();
                Utils.unloadRecord(record);

                try
                {
                    if (record.dirty)
                    {
                        record.save(Utils.replayFile(record.filename));
                        record.dirty = false;
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Check for scheduled records and countdown them.
     */
    private void checkScheduled()
    {
        Iterator<ScheduledRecording> it = CommonProxy.manager.scheduled.values().iterator();

        while (it.hasNext())
        {
            ScheduledRecording record = it.next();

            if (record.countdown % 20 == 0)
            {
                IMessage message = new PacketCaption("Starting in §7" + (record.countdown / 20));
                Dispatcher.sendTo(message, (EntityPlayerMP) record.player);
            }

            if (record.countdown <= 0)
            {
                record.run();
                CommonProxy.manager.recorders.put(record.player, record.recorder);
                Dispatcher.sendTo(new PacketPlayerRecording(true, record.recorder.record.filename), (EntityPlayerMP) record.player);

                it.remove();

                continue;
            }

            record.countdown--;
        }
    }

    /**
     * This is going to record the player actions
     */
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        EntityPlayer player = event.player;

        if (event.phase == Phase.START)
        {
            return;
        }

        if (!player.world.isRemote && CommonProxy.manager.recorders.containsKey(player))
        {
            RecordRecorder recorder = CommonProxy.manager.recorders.get(player);

            if (player.isDead)
            {
                CommonProxy.manager.stopRecording(player, true, true);
                Utils.broadcastInfo("recording.dead", recorder.record.filename);

                return;
            }

            recorder.record(player);
        }

        /* Update TEs in the model's TEISR */
        if (player.world.isRemote)
        {
            for (TEModel model : TileEntityModelItemStackRenderer.models.values())
            {
                model.model.update();
            }
        }
    }
}