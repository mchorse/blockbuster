package mchorse.blockbuster.recording;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.BreakBlockAction;
import mchorse.blockbuster.recording.actions.ChatAction;
import mchorse.blockbuster.recording.actions.DropAction;
import mchorse.blockbuster.recording.actions.PlaceBlockAction;
import mchorse.blockbuster.recording.actions.ShootArrowAction;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

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
            events.add(new BreakBlockAction(new BlockPos(event.x, event.y, event.z)));
        }
    }

    /**
     * Sorry, no interacting with block
     */

    /**
     * Event listener for Action.PLACE_BLOCK
     */
    @SubscribeEvent
    public void onPlayerPlacesBlock(PlaceEvent event)
    {
        EntityPlayer player = event.player;
        List<Action> events = CommonProxy.manager.getActions(player);

        if (!player.worldObj.isRemote && events != null)
        {
            BlockPos pos = new BlockPos(event.x, event.y, event.z);

            this.placeBlock(events, pos, event.placedBlock, event.blockMetadata);
        }
    }

    /**
     * Another event listener for Action.PLACE_BLOCK
     */
    @SubscribeEvent
    public void onPlayerPlacesMultiBlock(MultiPlaceEvent event)
    {
        EntityPlayer player = event.player;
        List<Action> events = CommonProxy.manager.getActions(player);

        if (!player.worldObj.isRemote && events != null)
        {
            List<BlockSnapshot> blocks = event.getReplacedBlockSnapshots();

            for (BlockSnapshot snapshot : blocks)
            {
                BlockPos pos = new BlockPos(snapshot.x, snapshot.y, snapshot.z);

                this.placeBlock(events, pos, snapshot.getCurrentBlock(), snapshot.meta);
            }
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
        EntityPlayer player = event.entityPlayer;
        List<Action> events = CommonProxy.manager.getActions(player);
        MovingObjectPosition target = event.target;

        if (!player.worldObj.isRemote && events != null && target != null && target.typeOfHit == MovingObjectType.BLOCK)
        {
            Item bucket = event.current.getItem();
            BlockPos pos = new BlockPos(target.blockX, target.blockY, target.blockZ).offset(target.sideHit);

            if (bucket == Items.lava_bucket)
            {
                this.placeBlock(events, pos, Blocks.flowing_lava, 0);
            }
            else if (bucket == Items.water_bucket)
            {
                this.placeBlock(events, pos, Blocks.flowing_water, 0);
            }
        }
    }

    /**
     * Place block in given event list
     */
    private void placeBlock(List<Action> events, BlockPos pos, Block block, int metadata)
    {
        String id = Block.blockRegistry.getNameForObject(block);

        events.add(new PlaceBlockAction(pos, (byte) metadata, id));
    }

    /**
     * Event listener for Action.SHOOT_ARROW
     */
    @SubscribeEvent
    public void onArrowLooseEvent(ArrowLooseEvent event) throws IOException
    {
        EntityPlayer player = event.entityPlayer;
        List<Action> events = CommonProxy.manager.getActions(player);

        if (!player.worldObj.isRemote && events != null)
        {
            events.add(new ShootArrowAction(event.charge));
        }
    }

    /**
     * Event listener for Action.DROP (when player drops the item from his
     * inventory)
     */
    @SubscribeEvent
    public void onItemTossEvent(ItemTossEvent event) throws IOException
    {
        EntityPlayer player = event.player;
        List<Action> events = CommonProxy.manager.getActions(player);

        if (!player.worldObj.isRemote && events != null)
        {
            events.add(new DropAction(event.entityItem.getEntityItem()));
        }
    }

    /**
     * Event listener for Action.CHAT (basically when the player enters
     * something in the chat)
     */
    @SubscribeEvent
    public void onServerChatEvent(ServerChatEvent event)
    {
        EntityPlayer player = event.player;
        List<Action> events = CommonProxy.manager.getActions(player);

        if (!player.worldObj.isRemote && events != null)
        {
            events.add(new ChatAction(event.message));
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