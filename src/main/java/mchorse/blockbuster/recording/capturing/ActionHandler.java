package mchorse.blockbuster.recording.capturing;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.capabilities.recording.IRecording;
import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.client.render.tileentity.TileEntityGunItemStackRenderer;
import mchorse.blockbuster.client.render.tileentity.TileEntityGunItemStackRenderer.GunEntry;
import mchorse.blockbuster.client.render.tileentity.TileEntityModelItemStackRenderer;
import mchorse.blockbuster.client.render.tileentity.TileEntityModelItemStackRenderer.TEModel;
import mchorse.blockbuster.recording.RecordManager;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.recording.RecordRecorder;
import mchorse.blockbuster.recording.RecordUtils;
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
import mchorse.blockbuster_pack.morphs.StructureMorph;
import mchorse.metamorph.api.events.MorphActionEvent;
import mchorse.metamorph.api.events.MorphEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.List;

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

    private int timer;

    /** 
     * Adds a world event listener  
     */
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        World world = event.getWorld();

        if (!world.isRemote)
        {
            world.addEventListener(new WorldEventListener(world));
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

        if (!player.world.isRemote && events != null)
        {
            events.add(new BreakBlockAction(event.getPos(), !player.isCreative()));
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

        if (!player.world.isRemote && events != null && !Blockbuster.recordAttackOnSwipe.get())
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
        if (!Blockbuster.recordCommands.get())
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
            CommonProxy.manager.abort(player);
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
        if (event.phase == Phase.START)
        {
            return;
        }

        CommonProxy.manager.tick();
        CommonProxy.scenes.tick();
    }

    /**
     * This is going to record the player actions
     */
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        if (event.phase == Phase.START)
        {
            return;
        }

        EntityPlayer player = event.player;
        boolean server = !player.world.isRemote;

        if (server && CommonProxy.manager.recorders.containsKey(player))
        {
            RecordRecorder recorder = CommonProxy.manager.recorders.get(player);

            if (player.isDead)
            {
                CommonProxy.manager.halt(player, true, true);
                RecordUtils.broadcastInfo("recording.dead", recorder.record.filename);
            }
            else
            {
                recorder.record(player);
            }
        }

        IRecording recording = Recording.get(player);
        RecordPlayer record = recording.getRecordPlayer();

        if (record != null)
        {
            record.next();

            if (record.isFinished() && server)
            {
                record.stopPlaying();
            }
        }

        /* Update TEs in the model's TEISR */
        if (player.world.isRemote)
        {
            this.updateClientTEISRs();
        }
        else
        {
            if (this.timer % 100 == 0)
            {
                StructureMorph.checkStructures();
                this.timer = 0;
            }

            this.timer += 1;
        }
    }

    @SideOnly(Side.CLIENT)
    private void updateClientTEISRs()
    {
        for (TEModel model : TileEntityModelItemStackRenderer.models.values())
        {
            model.model.update();
        }

        for (GunEntry model : TileEntityGunItemStackRenderer.models.values())
        {
            model.gun.getProps().update();
        }
    }
}