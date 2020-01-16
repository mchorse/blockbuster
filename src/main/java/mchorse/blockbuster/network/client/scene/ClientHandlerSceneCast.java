package mchorse.blockbuster.network.client.scene;

import com.sun.security.ntlm.Client;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.network.common.scene.PacketSceneCast;
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
                dashboard.directorPanel.openScene(message.scene, message.pos);
            }
            else
            {
                dashboard.openPanel(dashboard.directorPanel);
                dashboard.directorPanel.setScene(message.scene, message.pos);
            }
        }
        else if (ClientProxy.dashboard != null)
        {
            GuiDashboard dashboard = ClientProxy.dashboard;

            if (!message.open)
            {
                dashboard.createWorldPanels(Minecraft.getMinecraft(), false);
                dashboard.onOpen();
                dashboard.openPanel(dashboard.directorPanel);
                dashboard.directorPanel.setScene(message.scene, message.pos);
            }
            else
            {
                dashboard.directorPanel.set(message.scene, message.pos);
            }
        }
    }
}