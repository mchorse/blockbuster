package mchorse.blockbuster.network.client.scene;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.gui.dashboard.GuiBlockbusterPanels;
import mchorse.blockbuster.network.common.scene.PacketSceneCast;
import mchorse.mclib.client.gui.mclib.GuiDashboard;
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
 * cast to current director panel.
 */
public class ClientHandlerSceneCast extends ClientMessageHandler<PacketSceneCast>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketSceneCast message)
    {
        boolean opened = false;

        if (message.open && Minecraft.getMinecraft().currentScreen == null)
        {
            Minecraft.getMinecraft().displayGuiScreen(GuiDashboard.get());
            opened = true;
        }

        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        if (screen instanceof GuiDashboard)
        {
            GuiDashboard dashboard = (GuiDashboard) screen;

            if (opened)
            {
                dashboard.panels.setPanel(ClientProxy.panels.directorPanel);
                ClientProxy.panels.directorPanel.openScene(message.location);
            }
            else
            {
                dashboard.panels.setPanel(ClientProxy.panels.directorPanel);
                ClientProxy.panels.directorPanel.setScene(message.location);
            }
        }
        else if (GuiDashboard.dashboard != null)
        {
            GuiBlockbusterPanels dashboard = ClientProxy.panels;

            if (!message.open)
            {
                GuiDashboard.get().panels.setPanel(dashboard.directorPanel);
                dashboard.directorPanel.setScene(message.location);
            }
            else
            {
                dashboard.directorPanel.set(message.location);
            }
        }
    }
}