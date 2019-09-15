package mchorse.blockbuster.network.client.director;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.network.common.director.PacketDirectorCast;
import mchorse.mclib.network.ClientMessageHandler;
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
            ClientProxy.getDashboard(false).open();
            opened = true;
        }

        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        if (screen instanceof GuiDashboard)
        {
            GuiDashboard dashboard = (GuiDashboard) screen;

            if (opened)
            {
                dashboard.openPanel(dashboard.directorPanel);
                dashboard.directorPanel.openDirector(message.director, message.pos);
            }
            else
            {
                dashboard.openPanel(dashboard.directorPanel);
                dashboard.directorPanel.setDirector(message.director, message.pos);
            }
        }
        else if (ClientProxy.dashboard != null)
        {
            ClientProxy.dashboard.directorPanel.setDirector(message.director, message.pos);
        }
    }
}