package noname.blockbuster.recording;

import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

public class MocapEventHandler
{
    @SubscribeEvent
    public void onPlayerMountsSomething(EntityMountEvent event)
    {
        Side side = FMLCommonHandler.instance().getEffectiveSide();

        if (side == Side.SERVER && event.getEntityMounting() instanceof EntityPlayer)
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
