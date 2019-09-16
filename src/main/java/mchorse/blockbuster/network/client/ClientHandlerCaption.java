package mchorse.blockbuster.network.client;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.network.common.PacketCaption;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerCaption extends ClientMessageHandler<PacketCaption>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketCaption message)
    {
        String caption = message.caption == null ? "" : message.caption.getUnformattedText();

        ClientProxy.recordingOverlay.setVisible(!caption.isEmpty());
        ClientProxy.recordingOverlay.setCaption(caption, false);
    }
}