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
        GuiDashboard dashboard = GuiDashboard.get();
        GuiBlockbusterPanels panels = ClientProxy.panels;
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        boolean opened = message.open && screen == null;

        if (opened)
        {
            panels.directorPanel.openScene(message.location);
        }
        else if (screen == dashboard)
        {
            panels.directorPanel.setScene(message.location);
        }
        else
        {
            panels.directorPanel.set(message.location);
        }

        if (opened)
        {
            dashboard.panels.setPanel(panels.directorPanel);
            Minecraft.getMinecraft().displayGuiScreen(GuiDashboard.get());
        }
    }
}