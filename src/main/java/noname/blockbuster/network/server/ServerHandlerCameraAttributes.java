package noname.blockbuster.network.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.entity.EntityCamera;
import noname.blockbuster.network.common.PacketCameraAttributes;

/**
 * Someone, please write comments for every of client and server handlers for me :)
 */
public class ServerHandlerCameraAttributes extends ServerMessageHandler<PacketCameraAttributes>
{
    @Override
    public void run(EntityPlayerMP player, PacketCameraAttributes message)
    {
        Entity entity = player.worldObj.getEntityByID(message.id);

        if (entity instanceof EntityCamera)
        {
            ((EntityCamera) entity).setConfiguration(message.speed, message.accelerationRate, message.accelerationMax, message.canFly, true);
        }
    }
}