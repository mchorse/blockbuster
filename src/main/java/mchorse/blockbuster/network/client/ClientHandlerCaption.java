package mchorse.blockbuster.network.client;

import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.common.PacketCaption;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientHandlerCaption extends ClientMessageHandler<PacketCaption>
{
    @Override
    public void run(EntityPlayerSP player, PacketCaption message)
    {
        ClientProxy.recordingOverlay.setVisible(!message.caption.isEmpty());
        ClientProxy.recordingOverlay.setCaption(message.caption, false);
    }
}