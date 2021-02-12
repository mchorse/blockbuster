package mchorse.blockbuster.client.gui.dashboard;

import mchorse.mclib.client.gui.mclib.GuiDashboard;
import mchorse.mclib.client.gui.mclib.GuiDashboardPanel;
import net.minecraft.client.Minecraft;

public class GuiBlockbusterPanel extends GuiDashboardPanel
{
    public GuiBlockbusterPanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc, dashboard);
    }

    @Override
    public void appear()
    {
        GuiFirstTime.addOverlay(this.dashboard);
    }
}