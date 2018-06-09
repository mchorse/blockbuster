package mchorse.blockbuster.network.client.director;

import mchorse.blockbuster.client.gui.GuiDirector;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.director.PacketDirectorCast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Client handler director cast
 *
 * This client handler is responsible for transferring requested director block
 * cast to current {@link GuiDirector}.
 */
public class ClientHandlerDirectorCast extends ClientMessageHandler<PacketDirectorCast>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketDirectorCast message)
    {
        if (Minecraft.getMinecraft().currentScreen == null)
        {
            GuiDashboard dashboard = new GuiDashboard();
            dashboard.openPanel(dashboard.directorPanel);

            Minecraft.getMinecraft().displayGuiScreen(dashboard);
        }

        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        if (screen instanceof GuiDashboard)
        {
            ((GuiDashboard) screen).directorPanel.openDirector(message.director, message.pos);
        }
    }
}