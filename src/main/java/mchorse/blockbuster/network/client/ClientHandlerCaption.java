package mchorse.blockbuster.network.client;

import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.common.PacketCaption;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerCaption extends ClientMessageHandler<PacketCaption>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketCaption message)
    {
        ClientProxy.recordingOverlay.setVisible(!message.caption.isEmpty());
        ClientProxy.recordingOverlay.setCaption(message.caption, false);
    }
}