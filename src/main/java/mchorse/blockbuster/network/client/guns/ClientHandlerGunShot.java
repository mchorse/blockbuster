package mchorse.blockbuster.network.client.guns;

import mchorse.blockbuster.capabilities.gun.Gun;
import mchorse.blockbuster.capabilities.gun.IGun;
import mchorse.blockbuster.network.common.guns.PacketGunShot;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerGunShot extends ClientMessageHandler<PacketGunShot>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketGunShot message)
    {
        Entity entity = player.world.getEntityByID(message.entity);

        if (entity instanceof EntityLivingBase)
        {
            EntityLivingBase base = (EntityLivingBase) entity;
            IGun gun = Gun.get(base.getHeldItemMainhand());

            if (gun != null)
            {
                gun.getProps().shot();
            }
        }
    }
}