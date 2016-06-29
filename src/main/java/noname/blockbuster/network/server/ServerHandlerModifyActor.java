package noname.blockbuster.network.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.entity.EntityActor;
import noname.blockbuster.network.common.PacketModifyActor;

public class ServerHandlerModifyActor extends ServerMessageHandler<PacketModifyActor>
{
    @Override
    public void run(EntityPlayerMP player, PacketModifyActor message)
    {
        Entity entity = player.worldObj.getEntityByID(message.id);

        if (entity instanceof EntityActor)
        {
            ((EntityActor) entity).modify(message.filename, message.name, message.skin, message.invulnerable, true);
        }
    }
}