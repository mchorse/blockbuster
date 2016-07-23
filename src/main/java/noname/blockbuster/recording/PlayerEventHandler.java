package noname.blockbuster.recording;

import java.io.IOException;
import java.util.List;

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
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import noname.blockbuster.recording.actions.Action;
import noname.blockbuster.recording.actions.BreakBlockAction;
import noname.blockbuster.recording.actions.ChatAction;
import noname.blockbuster.recording.actions.DropAction;
import noname.blockbuster.recording.actions.InteractBlockAction;
import noname.blockbuster.recording.actions.LogoutAction;
import noname.blockbuster.recording.actions.MountingAction;
import noname.blockbuster.recording.actions.PlaceBlockAction;
import noname.blockbuster.recording.actions.ShootArrowAction;

/**
 * Event handler for recording purposes.
 *
 * This event handler listens to different events and then writes them to
 * the recording event list (which in turn are being written to the disk
 * by RecordThread).
 *
 * Taken from Mocap mod and rewritten.
 */
public class PlayerEventHandler
{
    /**
     * Event listener for Action.BREAK_BLOCK
     */
    @SubscribeEvent
    public void onPlayerBreaksBlock(BreakEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
        {
            return;
        }

        List<Action> events = Mocap.getActionListForPlayer(event.getPlayer());

        if (events != null)
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
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
        {
            return;
        }

        EntityPlayer player = event.getEntityPlayer();
        List<Action> events = Mocap.getActionListForPlayer(player);

        if (events != null)
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
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
        {
            return;
        }

        EntityPlayer player = event.getPlayer();
        List<Action> events = Mocap.getActionListForPlayer(player);

        if (events != null)
        {
            IBlockState state = event.getPlacedBlock();
            Block block = state.getBlock();

            this.placeBlock(events, event.getPos(), block, state);
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
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
        {
            return;
        }

        EntityPlayer player = event.getEntityPlayer();
        List<Action> events = Mocap.getActionListForPlayer(player);
        RayTraceResult target = event.getTarget();

        if (events != null && target != null && target.typeOfHit == Type.BLOCK)
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
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
        {
            return;
        }

        if (event.getEntityMounting() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.getEntityMounting();
            List<Action> events = Mocap.getActionListForPlayer(player);

            if (events != null)
            {
                events.add(new MountingAction(event.getEntityBeingMounted().getUniqueID(), event.isMounting()));
            }
        }
    }

    /**
     * Event listener for Action.LOGOUT (that's obvious)
     */
    @SubscribeEvent
    public void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
        {
            return;
        }

        List<Action> events = Mocap.getActionListForPlayer(event.player);

        if (events != null)
        {
            events.add(new LogoutAction());
        }
    }

    /**
     * Doesn't work for some reason
     *
     * I'll fix it later
     */
    @SubscribeEvent
    public void onArrowLooseEvent(ArrowLooseEvent event) throws IOException
    {
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
        {
            return;
        }

        List<Action> events = Mocap.getActionListForPlayer(event.getEntityPlayer());

        if (events != null)
        {
            Action action = new ShootArrowAction(event.getCharge());

            events.add(action);
        }
    }

    /**
     * Event listener for Action.DROP (when player drops the item from his
     * inventory)
     */
    @SubscribeEvent
    public void onItemTossEvent(ItemTossEvent event) throws IOException
    {
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
        {
            return;
        }

        List<Action> events = Mocap.getActionListForPlayer(event.getPlayer());

        if (events != null)
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
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
        {
            return;
        }

        List<Action> events = Mocap.getActionListForPlayer(event.getPlayer());

        if (events != null)
        {
            events.add(new ChatAction(event.getMessage()));
        }
    }
}
