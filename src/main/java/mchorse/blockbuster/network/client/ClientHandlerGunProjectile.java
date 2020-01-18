package mchorse.blockbuster.network.client;

import mchorse.blockbuster.common.entity.EntityGunProjectile;
import mchorse.blockbuster.network.common.guns.PacketGunProjectile;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerGunProjectile extends ClientMessageHandler<PacketGunProjectile>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketGunProjectile message)
    {
        Entity entity = player.world.getEntityByID(message.id);

        if (entity instanceof EntityGunProjectile)
        {
            ((EntityGunProjectile) entity).morph.set(message.morph, true);
        }
    }
}