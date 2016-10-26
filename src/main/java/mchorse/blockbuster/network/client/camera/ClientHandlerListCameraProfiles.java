package mchorse.blockbuster.network.client.camera;

import mchorse.blockbuster.client.gui.GuiPlayback;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.camera.PacketListCameraProfiles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Client handler list camera profiles
 *
 * This client handler is responsible for getting requested list of available
 * camera profiles to the {@link GuiPlayback} (it's needed for tab completion).
 */
public class ClientHandlerListCameraProfiles extends ClientMessageHandler<PacketListCameraProfiles>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketListCameraProfiles message)
    {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        if (screen instanceof GuiPlayback)
        {
            ((GuiPlayback) screen).setCompletions(message.profiles);
        }
    }
}