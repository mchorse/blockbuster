package mchorse.blockbuster.recording;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.AttackAction;
import mchorse.blockbuster.recording.actions.BreakBlockAction;
import mchorse.blockbuster.recording.actions.ChatAction;
import mchorse.blockbuster.recording.actions.DropAction;
import mchorse.blockbuster.recording.actions.InteractBlockAction;
import mchorse.blockbuster.recording.actions.MountingAction;
import mchorse.blockbuster.recording.actions.PlaceBlockAction;
import mchorse.blockbuster.recording.actions.ShootArrowAction;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

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
     * Event listener for Action.BREAK_BLOCK
     */
    @SubscribeEvent
    public void onPlayerBreaksBlock(BreakEvent event)
    {
        EntityPlayer player = event.getPlayer();
        List<Action> events = CommonProxy.manager.getActions(player);

        if (!player.worldObj.isRemote && events != null)
        {
            events.add(new BreakBlockAction(event.getPos()));
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

        if (!player.worldObj.isRemote && events != null)
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

        if (!player.worldObj.isRemote && events != null)
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

        if (!player.worldObj.isRemote && events != null)
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

        if (!player.worldObj.isRemote && events != null && !Blockbuster.proxy.config.record_attack_on_swipe)
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

        if (!player.worldObj.isRemote && events != null && target != null && target.typeOfHit == Type.BLOCK)
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

            if (!player.worldObj.isRemote && events != null)
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

        if (!player.worldObj.isRemote && events != null)
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

        if (!player.worldObj.isRemote && events != null)
        {
            events.add(new DropAction(event.getEntityItem().getEntityItem()));
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

        if (!player.worldObj.isRemote && events != null)
        {
            events.add(new ChatAction(event.getMessage()));
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

        if (!player.worldObj.isRemote)
        {
            CommonProxy.manager.abortRecording(player);
        }
    }

    /**
     * Event listener for world tick event.
     *
     * This is probably not the optimal solution, but I'm not really sure how
     * to schedule things in Minecraft other way than timers and ticks.
     *
     * This method is responsible for scheduling record unloading.
     */
    @SubscribeEvent
    public void onWorldTick(ServerTickEvent event)
    {
        if (CommonProxy.manager.records.isEmpty() || !Blockbuster.proxy.config.record_unload)
        {
            return;
        }

        Iterator<Map.Entry<String, Record>> iterator = CommonProxy.manager.records.entrySet().iterator();

        while (iterator.hasNext())
        {
            Record record = iterator.next().getValue();

            record.unload--;

            if (record.unload <= 0)
            {
                iterator.remove();
                Utils.unloadRecord(record);
            }
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

        if (!player.worldObj.isRemote && CommonProxy.manager.recorders.containsKey(player))
        {
            RecordRecorder recorder = CommonProxy.manager.recorders.get(player);

            if (player.isDead)
            {
                CommonProxy.manager.stopRecording(player, true);
                Utils.broadcastInfo("recording.dead", recorder.record.filename);

                return;
            }

            recorder.record(player);
        }
    }
}