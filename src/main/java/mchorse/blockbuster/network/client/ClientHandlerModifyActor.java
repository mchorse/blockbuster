package mchorse.blockbuster.network.client;

import mchorse.blockbuster.entity.EntityActor;
import mchorse.blockbuster.network.common.PacketModifyActor;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientHandlerModifyActor extends ClientMessageHandler<PacketModifyActor>
{
    @Override
    public void run(EntityPlayerSP player, PacketModifyActor message)
    {
        EntityActor actor = (EntityActor) player.worldObj.getEntityByID(message.id);

        actor.modify(message.filename, message.name, message.skin, message.model, message.invulnerable, false);
    }
}
