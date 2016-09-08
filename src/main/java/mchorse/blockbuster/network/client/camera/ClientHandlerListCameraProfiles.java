package mchorse.blockbuster.network.client.camera;

import mchorse.blockbuster.client.gui.GuiPlayback;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.camera.PacketListCameraProfiles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;

public class ClientHandlerListCameraProfiles extends ClientMessageHandler<PacketListCameraProfiles>
{
    @Override
    public void run(EntityPlayerSP player, PacketListCameraProfiles message)
    {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        if (screen instanceof GuiPlayback)
        {
            ((GuiPlayback) screen).setCompletions(message.profiles);
        }
    }
}
