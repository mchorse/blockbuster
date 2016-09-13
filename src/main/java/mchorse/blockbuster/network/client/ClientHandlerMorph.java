package mchorse.blockbuster.network.client;

import mchorse.blockbuster.capabilities.morphing.IMorphing;
import mchorse.blockbuster.capabilities.morphing.MorphingProvider;
import mchorse.blockbuster.network.common.PacketMorph;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientHandlerMorph extends ClientMessageHandler<PacketMorph>
{
    @Override
    public void run(EntityPlayerSP player, PacketMorph message)
    {
        IMorphing capability = player.getCapability(MorphingProvider.MORPHING_CAP, null);

        if (capability != null)
        {
            capability.setModel(message.model);
            capability.setSkin(message.skin);
        }
    }
}
