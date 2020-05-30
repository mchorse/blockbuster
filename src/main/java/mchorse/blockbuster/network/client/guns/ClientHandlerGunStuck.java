package mchorse.blockbuster.network.client.guns;

import mchorse.blockbuster.common.entity.EntityGunProjectile;
import mchorse.blockbuster.network.common.guns.PacketGunStuck;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerGunStuck extends ClientMessageHandler<PacketGunStuck>
{
	@Override
	@SideOnly(Side.CLIENT)
	public void run(EntityPlayerSP player, PacketGunStuck packet)
	{
		Entity entity = player.world.getEntityByID(packet.id);

		if (entity instanceof EntityGunProjectile)
		{
			EntityGunProjectile bullet = (EntityGunProjectile) entity;

			bullet.stuck = true;
			bullet.posX = bullet.targetX = packet.x;
			bullet.posY = bullet.targetY = packet.y;
			bullet.posZ = bullet.targetZ = packet.z;
			bullet.setPosition(packet.x, packet.y, packet.z);
		}
	}
}