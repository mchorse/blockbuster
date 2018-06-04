package mchorse.blockbuster.client.gui.dashboard.panels;

import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import net.minecraft.client.Minecraft;

public class GuiDashboardPanel extends GuiElement
{
    public GuiDashboardPanel(Minecraft mc)
    {
        super(mc);
        this.createChildren();
    }

    public boolean needsBackground()
    {
        return true;
    }

    public void init()
    {}

    public void close()
    {}
}