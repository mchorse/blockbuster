package noname.blockbuster.recording;

import java.io.IOException;
import java.util.List;

import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

public class MocapEventHandler
{
	@SubscribeEvent
	public void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event)
	{
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER)
		{
			List<Action> aList = Mocap.getActionListForPlayer(event.player);
			if (aList != null)
			{
				Action ma = new Action((byte) 6);
				aList.add(ma);
			}
		}
	}

	/*
	 * @SubscribeEvent public void onLivingPlaceBlockEvent(LivingPlaceBlockEvent
	 * event) { Side side = FMLCommonHandler.instance().getEffectiveSide(); if
	 * ((side == Side.SERVER) && ((event.entityLiving instanceof
	 * EntityPlayerMP))) { EntityPlayerMP thePlayer = (EntityPlayerMP)
	 * event.entityLiving; List<Action> aList =
	 * Mocap.getActionListForPlayer(thePlayer); if (aList != null) { Action ma =
	 * new Action((byte) 7); event.theItem.writeToNBT(ma.itemData); ma.xCoord =
	 * event.xCoord; ma.yCoord = event.yCoord; ma.zCoord = event.zCoord;
	 * aList.add(ma); } } }
	 */

	@SubscribeEvent
	public void onArrowLooseEvent(ArrowLooseEvent ev) throws IOException
	{
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER)
		{
			List<Action> aList = Mocap.getActionListForPlayer(ev.getEntityPlayer());

			if (aList != null)
			{
				Action ma = new Action((byte) 5);
				ma.arrowCharge = ev.getCharge();
				aList.add(ma);
			}
		}
	}

	@SubscribeEvent
	public void onItemTossEvent(ItemTossEvent ev) throws IOException
	{
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER)
		{
			List<Action> aList = Mocap.getActionListForPlayer(ev.getPlayer());
			if (aList != null)
			{
				Action ma = new Action((byte) 3);
				ev.getEntityItem().getEntityItem().writeToNBT(ma.itemData);
				aList.add(ma);
			}
		}
	}

	@SubscribeEvent
	public void onServerChatEvent(ServerChatEvent ev)
	{
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER)
		{
			List<Action> aList = Mocap.getActionListForPlayer(ev.getPlayer());
			if (aList != null)
			{
				Action ma = new Action((byte) 1);
				ma.message = ev.getMessage();
				aList.add(ma);
			}
		}
	}
}
