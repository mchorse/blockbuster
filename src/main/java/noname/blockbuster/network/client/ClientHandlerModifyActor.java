package noname.blockbuster.network.client;

import net.minecraft.client.entity.EntityPlayerSP;
import noname.blockbuster.entity.EntityActor;
import noname.blockbuster.network.common.PacketModifyActor;

public class ClientHandlerModifyActor extends ClientMessageHandler<PacketModifyActor>
{
    @Override
    public void run(EntityPlayerSP player, PacketModifyActor message)
    {
        EntityActor actor = (EntityActor) player.worldObj.getEntityByID(message.id);

        actor.modify(message.invulnerable, message.name, false);
    }
}
