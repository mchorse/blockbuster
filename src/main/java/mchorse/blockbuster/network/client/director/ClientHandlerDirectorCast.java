package mchorse.blockbuster.network.client.director;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.common.ClientProxy;
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
        boolean opened = false;

        if (Minecraft.getMinecraft().currentScreen == null)
        {
            GuiDashboard dashboard = ClientProxy.getDashboard(false);

            Minecraft.getMinecraft().displayGuiScreen(dashboard);
            opened = true;
        }

        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        if (screen instanceof GuiDashboard)
        {
            GuiDashboard dashboard = (GuiDashboard) screen;

            if (opened)
            {
                dashboard.openPanel(dashboard.directorPanel.openDirector(message.director, message.pos));
            }
            else
            {
                dashboard.openPanel(dashboard.directorPanel.setDirector(message.director, message.pos));
            }
        }
    }
}