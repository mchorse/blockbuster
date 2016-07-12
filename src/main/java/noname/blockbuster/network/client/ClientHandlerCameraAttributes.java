package noname.blockbuster.network.client;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import noname.blockbuster.entity.EntityCamera;
import noname.blockbuster.network.common.PacketCameraAttributes;

public class ClientHandlerCameraAttributes extends ClientMessageHandler<PacketCameraAttributes>
{
    @Override
    public void run(EntityPlayerSP player, PacketCameraAttributes message)
    {
        Entity entity = player.worldObj.getEntityByID(message.id);

        if (entity instanceof EntityCamera)
        {
            EntityCamera camera = (EntityCamera) entity;

            camera.setConfiguration(message.name, message.speed, message.accelerationRate, message.accelerationMax, message.canFly, false);
        }
    }
}