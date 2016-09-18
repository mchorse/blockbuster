package mchorse.blockbuster.network.server;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.common.PacketModifyActor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Server handler modify actor
 *
 * This handler is responsible for injecting all of the values from recieved
 * message into actor entity via special defined method.
 */
public class ServerHandlerModifyActor extends ServerMessageHandler<PacketModifyActor>
{
    @Override
    public void run(EntityPlayerMP player, PacketModifyActor message)
    {
        Entity entity = player.worldObj.getEntityByID(message.id);

        if (entity instanceof EntityActor)
        {
            ((EntityActor) entity).modify(message.model, message.skin, message.invisible, true);
        }
    }
}