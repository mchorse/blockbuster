package mchorse.blockbuster.network.server.scene;

import mchorse.blockbuster.network.common.scene.PacketScenePause;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerScenePause extends ServerMessageHandler<PacketScenePause>
{
	@Override
	public void run(EntityPlayerMP player, PacketScenePause packet)
	{
		Scene scene = packet.get(player.worldObj);

		if (!scene.isPlaying())
		{
			scene.resume(-1);
		}
		else
		{
			scene.pause();
		}
	}
}