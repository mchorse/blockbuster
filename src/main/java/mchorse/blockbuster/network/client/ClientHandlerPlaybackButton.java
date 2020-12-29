package mchorse.blockbuster.network.client;

import mchorse.blockbuster.aperture.gui.GuiPlayback;
import mchorse.blockbuster.network.common.PacketPlaybackButton;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerPlaybackButton extends ClientMessageHandler<PacketPlaybackButton>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketPlaybackButton message)
    {
        GuiPlayback playback = new GuiPlayback();

        playback.setLocation(message.location, message.scenes);
        Minecraft.getMinecraft().displayGuiScreen(playback);
    }
}
