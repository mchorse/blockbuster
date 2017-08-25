package mchorse.blockbuster.aperture.network.client;

import mchorse.aperture.camera.destination.ServerDestination;
import mchorse.aperture.network.common.PacketCameraProfileList;
import mchorse.blockbuster.aperture.gui.GuiPlayback;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerCameraProfileList extends ClientMessageHandler<PacketCameraProfileList>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketCameraProfileList message)
    {
        GuiScreen current = Minecraft.getMinecraft().currentScreen;

        if (current instanceof GuiPlayback)
        {
            GuiPlayback gui = (GuiPlayback) current;

            for (String filename : message.cameras)
            {
                gui.profiles.add(new ServerDestination(filename));
            }

            gui.area.setSize(gui.profiles.size());
            gui.selectProfile();
        }
    }
}