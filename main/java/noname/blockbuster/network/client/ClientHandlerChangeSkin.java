package noname.blockbuster.network.client;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import noname.blockbuster.entity.ActorEntity;
import noname.blockbuster.network.common.ChangeSkin;

public class ClientHandlerChangeSkin extends ClientMessageHandler<ChangeSkin>
{
    @Override
    public void run(EntityPlayerSP player, ChangeSkin message)
    {
        Entity entity = player.worldObj.getEntityByID(message.id);

        if (entity instanceof ActorEntity)
        {
            ActorEntity actor = (ActorEntity) entity;

            actor.setSkin(message.skin, false);
        }
    }
}