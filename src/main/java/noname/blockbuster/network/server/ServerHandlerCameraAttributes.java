package noname.blockbuster.network.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import noname.blockbuster.entity.EntityCamera;
import noname.blockbuster.item.ItemCameraConfig;
import noname.blockbuster.network.common.PacketCameraAttributes;

public class ServerHandlerCameraAttributes extends ServerMessageHandler<PacketCameraAttributes>
{
    @Override
    public void run(EntityPlayerMP player, PacketCameraAttributes message)
    {
        Entity entity = player.worldObj.getEntityByID(message.id);
        if (entity instanceof EntityCamera)
        {
            EntityCamera camera = (EntityCamera) entity;

            ItemStack item = player.getHeldItemMainhand();

            if (item != null && item.getItem() instanceof ItemCameraConfig)
            {
                camera.setConfiguration(message.speed, message.accelerationRate, message.accelerationMax, message.canFly, true);
            }
        }
    }
}