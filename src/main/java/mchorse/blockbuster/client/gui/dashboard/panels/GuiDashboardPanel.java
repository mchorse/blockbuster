package mchorse.blockbuster.client.gui.dashboard.panels;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import net.minecraft.client.Minecraft;

public class GuiDashboardPanel extends GuiElement
{
    protected GuiDashboard dashboard;

    public GuiDashboardPanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc);
        this.createChildren();
        this.dashboard = dashboard;
    }

    public boolean needsBackground()
    {
        return true;
    }

    public void appear()
    {}

    public void disappear()
    {}

    public void open()
    {}

    public void close()
    {}
}