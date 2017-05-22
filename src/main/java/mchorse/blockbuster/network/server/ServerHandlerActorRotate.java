package mchorse.blockbuster.network.server;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.common.PacketActorRotate;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerActorRotate extends ServerMessageHandler<PacketActorRotate>
{
    @Override
    public void run(EntityPlayerMP player, PacketActorRotate message)
    {
        EntityActor actor = (EntityActor) player.world.getEntityByID(message.id);

        if (actor != null)
        {
            actor.setPositionAndRotation(actor.posX, actor.posY, actor.posZ, message.yaw, message.pitch);
        }
    }
}