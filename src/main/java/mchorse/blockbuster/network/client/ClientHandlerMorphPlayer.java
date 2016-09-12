package mchorse.blockbuster.network.client;

import mchorse.blockbuster.actor.IMorphing;
import mchorse.blockbuster.actor.MorphingProvider;
import mchorse.blockbuster.network.common.PacketMorphPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;

public class ClientHandlerMorphPlayer extends ClientMessageHandler<PacketMorphPlayer>
{
    @Override
    public void run(EntityPlayerSP player, PacketMorphPlayer message)
    {
        Entity entity = player.worldObj.getEntityByID(message.id);
        IMorphing capability = entity.getCapability(MorphingProvider.MORPHING_CAP, null);

        if (capability != null)
        {
            capability.setModel(message.model);
            capability.setSkin(message.skin);
        }
    }
}
