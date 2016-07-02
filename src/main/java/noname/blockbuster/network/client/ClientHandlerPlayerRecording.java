package noname.blockbuster.network.client;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.ClientProxy;
import noname.blockbuster.network.common.PacketPlayerRecording;

@SideOnly(Side.CLIENT)
public class ClientHandlerPlayerRecording extends ClientMessageHandler<PacketPlayerRecording>
{
    @Override
    public void run(EntityPlayerSP player, PacketPlayerRecording message)
    {
        ClientProxy.recordingOverlay.setVisible(message.recording);
        ClientProxy.recordingOverlay.setCaption(message.filename);
    }
}