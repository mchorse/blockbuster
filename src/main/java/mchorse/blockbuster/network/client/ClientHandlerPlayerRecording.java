package mchorse.blockbuster.network.client;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.actor.IMorphing;
import mchorse.blockbuster.actor.MorphingProvider;
import mchorse.blockbuster.network.common.PacketPlayerRecording;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientHandlerPlayerRecording extends ClientMessageHandler<PacketPlayerRecording>
{
    @Override
    public void run(EntityPlayerSP player, PacketPlayerRecording message)
    {
        ClientProxy.recordingOverlay.setVisible(message.recording);
        ClientProxy.recordingOverlay.setCaption(message.filename);

        if (!message.recording)
        {
            IMorphing capability = player.getCapability(MorphingProvider.MORPHING_CAP, null);
            capability.reset();
        }
    }
}