package noname.blockbuster.network.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.entity.EntityActor;
import noname.blockbuster.network.common.PacketChangeSkin;

public class ServerHandlerChangeSkin extends ServerMessageHandler<PacketChangeSkin>
{
    @Override
    public void run(EntityPlayerMP player, PacketChangeSkin message)
    {
        Entity entity = player.worldObj.getEntityByID(message.id);

        if (entity instanceof EntityActor)
        {
            EntityActor actor = (EntityActor) entity;

            actor.setSkin(message.skin, true);
        }
    }
}