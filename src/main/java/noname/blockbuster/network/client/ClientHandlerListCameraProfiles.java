package noname.blockbuster.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import noname.blockbuster.client.gui.GuiPlayback;
import noname.blockbuster.network.common.PacketListCameraProfiles;

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
