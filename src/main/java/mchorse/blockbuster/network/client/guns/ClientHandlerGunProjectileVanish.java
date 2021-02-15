package mchorse.blockbuster.network.client.guns;

import mchorse.blockbuster.common.entity.EntityGunProjectile;
import mchorse.blockbuster.network.common.guns.PacketGunProjectileVanish;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerGunProjectileVanish extends ClientMessageHandler<PacketGunProjectileVanish>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketGunProjectileVanish message)
    {
        Entity entity = player.world.getEntityByID(message.id);

        if (entity instanceof EntityGunProjectile)
        {
            EntityGunProjectile projectile = (EntityGunProjectile) entity;

            projectile.vanish = true;
            projectile.vanishDelay = message.delay;
        }
    }
}