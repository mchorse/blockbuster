package noname.blockbuster.network.server;

import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.entity.EntityActor;
import noname.blockbuster.network.common.PacketModifyActor;

public class ServerHandlerModifyActor extends ServerMessageHandler<PacketModifyActor>
{
    @Override
    public void run(EntityPlayerMP player, PacketModifyActor message)
    {
        EntityActor actor = (EntityActor) player.worldObj.getEntityByID(message.id);

        actor.modify(message.invulnerable, message.name, message.skin, true);
    }
}