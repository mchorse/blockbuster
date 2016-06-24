package noname.blockbuster.network.client;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import noname.blockbuster.entity.EntityActor;
import noname.blockbuster.network.common.PacketChangeSkin;

public class ClientHandlerChangeSkin extends ClientMessageHandler<PacketChangeSkin>
{
    @Override
    public void run(EntityPlayerSP player, PacketChangeSkin message)
    {
        Entity entity = player.worldObj.getEntityByID(message.id);

        if (entity instanceof EntityActor)
        {
            EntityActor actor = (EntityActor) entity;

            actor.setSkin(message.skin, false);
        }
    }
}