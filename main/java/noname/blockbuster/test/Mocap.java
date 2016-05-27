package noname.blockbuster.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class Mocap
{
	public static Map<EntityPlayer, Recorder> recordThreads = Collections.synchronizedMap(new HashMap());
	public static final short signature = 3208;
	
	public static List<Action> getActionListForPlayer(EntityPlayer ep)
	{
		Recorder aRecorder = recordThreads.get(ep);

		if (aRecorder == null)
		{
			return null;
		}

		return aRecorder.eventsList;
	}

	public static void broadcastMessage(String message)
	{
		ITextComponent chatMessage = new TextComponentString(message);
		PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

		for (String username : players.getAllUsernames())
		{
			EntityPlayerMP player = players.getPlayerByUsername(username);

			if (player != null)
			{
				player.addChatMessage(chatMessage);
			}
		}
	}
	
	public static EntityEquipmentSlot getSlotByIndex(int index)
	{
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values())
		{
			if (slot.func_188452_c() == index) return slot;
		}
		
		return null;
	}
}
