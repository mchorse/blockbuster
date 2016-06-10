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
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

public class MocapEventHandler
{
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
        List<Action> aList = Mocap.getActionListForPlayer(player);

        if (aList != null)
        {
            ItemStack item = event.getItemStack();
            Action ma;

            if (item != null && item.getItem() instanceof ItemBlock)
            {
                BlockPos pos = event.getPos().offset(event.getFace());

                ma = new Action(Action.PLACE_BLOCK);
                ma.xCoord = pos.getX();
                ma.yCoord = pos.getY();
                ma.zCoord = pos.getZ();

                item.writeToNBT(ma.itemData);
            }
            else
            {
                BlockPos pos = event.getPos();

                ma = new Action(Action.INTERACT_BLOCK);
                ma.xCoord = pos.getX();
                ma.yCoord = pos.getY();
                ma.zCoord = pos.getZ();
            }

            aList.add(ma);
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
            List<Action> aList = Mocap.getActionListForPlayer(player);

            if (aList != null)
            {
                Action ma = new Action(Action.MOUNTING);
                ma.target = event.getEntityBeingMounted().getUniqueID();
                ma.armorSlot = event.isMounting() ? 1 : 0;
                aList.add(ma);
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

        List<Action> aList = Mocap.getActionListForPlayer(event.player);

        if (aList != null)
        {
            Action ma = new Action(Action.LOGOUT);
            aList.add(ma);
        }
    }

    /**
     * Doesn't work for some reason
     */
    @SubscribeEvent
    public void onArrowLooseEvent(ArrowLooseEvent ev) throws IOException
    {
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
        {
            return;
        }

        List<Action> aList = Mocap.getActionListForPlayer(ev.getEntityPlayer());

        if (aList != null)
        {
            Action ma = new Action(Action.SHOOTARROW);
            ma.arrowCharge = ev.getCharge();
            aList.add(ma);
        }
    }

    /**
     * Event listener for Action.DROP (when player drops the item from his
     * inventory)
     */
    @SubscribeEvent
    public void onItemTossEvent(ItemTossEvent ev) throws IOException
    {
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
        {
            return;
        }

        List<Action> aList = Mocap.getActionListForPlayer(ev.getPlayer());

        if (aList != null)
        {
            Action ma = new Action(Action.DROP);
            ev.getEntityItem().getEntityItem().writeToNBT(ma.itemData);
            aList.add(ma);
        }
    }

    /**
     * Event listener for Action.CHAT (basically when the player enters
     * something in the chat)
     */
    @SubscribeEvent
    public void onServerChatEvent(ServerChatEvent ev)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
        {
            return;
        }

        List<Action> aList = Mocap.getActionListForPlayer(ev.getPlayer());

        if (aList != null)
        {
            Action ma = new Action(Action.CHAT);
            ma.message = ev.getMessage();
            aList.add(ma);
        }
    }
}
