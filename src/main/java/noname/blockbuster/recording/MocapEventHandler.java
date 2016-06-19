package noname.blockbuster.recording;

import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
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

/**
 * Event handler for recording purposes.
 *
 * This event handler listens to different events and then writes them to
 * the recording event list (which in turn are being written to the disk
 * by RecordThread).
 *
 * Taken from Mocap mod.
 */
public class MocapEventHandler
{
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
            ItemStack item = event.getItemStack();
            Action action;

            if (item != null && item.getItem() instanceof ItemBlock)
            {
                BlockPos pos = event.getPos().offset(event.getFace());
                byte metadata = (byte) item.getMetadata();
                byte facing = (byte) event.getFace().getIndex();

                action = new PlaceBlockAction(pos, metadata, facing, item);
            }
            else
            {
                action = new InteractBlockAction(event.getPos());
            }

            events.add(action);
        }
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
    public void onArrowLooseEvent(ArrowLooseEvent ev) throws IOException
    {
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
        {
            return;
        }

        List<Action> evemts = Mocap.getActionListForPlayer(ev.getEntityPlayer());

        if (evemts != null)
        {
            Action action = new Action(Action.SHOOTARROW);

            evemts.add(action);
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
