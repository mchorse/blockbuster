package noname.blockbuster.network.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.entity.ActorEntity;
import noname.blockbuster.network.common.ChangeSkin;

public class ServerHandlerChangeSkin extends ServerMessageHandler<ChangeSkin>
{
    @Override
    public void run(EntityPlayerMP player, ChangeSkin message)
    {
        Entity entity = player.worldObj.getEntityByID(message.id);

        if (entity instanceof ActorEntity)
        {
            ActorEntity actor = (ActorEntity) entity;

            actor.setSkin(message.skin, true);
        }
    }
}