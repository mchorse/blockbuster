package noname.blockbuster.network.client;

import net.minecraft.client.entity.EntityPlayerSP;
import noname.blockbuster.ClientProxy;
import noname.blockbuster.network.common.PacketPlayerRecording;

public class ClientHandlerPlayerRecording extends ClientMessageHandler<PacketPlayerRecording>
{
    @Override
    public void run(EntityPlayerSP player, PacketPlayerRecording message)
    {
        ClientProxy.recordingOverlay.setVisible(message.recording);
        ClientProxy.recordingOverlay.setCaption(message.filename);

        if (!message.recording)
        {
            ClientProxy.playerRender.reset();
        }
    }
}