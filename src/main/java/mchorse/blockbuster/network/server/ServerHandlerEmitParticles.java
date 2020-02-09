package mchorse.blockbuster.network.server;

import mchorse.blockbuster.network.common.PacketEmitParticles;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerEmitParticles extends ServerMessageHandler<PacketEmitParticles>
{
	@Override
	public void run(EntityPlayerMP player, PacketEmitParticles message)
	{
		player.getServerWorld().spawnParticle(message.type, true, message.x, message.y, message.z, message.count, message.dx, message.dy, message.dz, message.speed, message.arguments);
	}
}