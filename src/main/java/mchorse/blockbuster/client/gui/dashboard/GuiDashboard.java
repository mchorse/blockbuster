package mchorse.blockbuster.client.gui.dashboard;

import mchorse.blockbuster.client.gui.framework.GuiBase;
import mchorse.blockbuster.client.gui.framework.GuiDelegateElement;
import mchorse.blockbuster.client.gui.framework.GuiElement;
import mchorse.blockbuster.client.gui.utils.Resizer;
import mchorse.blockbuster.client.gui.utils.Resizer.UnitMeasurement;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiDashboard extends GuiBase
{
    public static final ResourceLocation ICONS = new ResourceLocation("blockbuster", "textures/gui/dashboard/icons.png");

    public GuiDelegateElement panel;
    public GuiDashboardSidebar sidebar;

    public GuiElement mainPanel;
    public GuiElement directorPanel;
    public GuiElement modelPanel;

    public GuiDashboard()
    {
        Minecraft mc = Minecraft.getMinecraft();
        Resizer panelResizer = new Resizer().set(32, 0, 1, 1).setParent(this.area);
        panelResizer.w.set(1, UnitMeasurement.PERCENTAGE, -32);
        panelResizer.h.set(1, UnitMeasurement.PERCENTAGE);

        this.mainPanel = new GuiMainPanel(mc);
        this.directorPanel = new GuiDirectorPanel(mc);
        this.modelPanel = new GuiModelPanel(mc);

        this.panel = new GuiDelegateElement(mc, this.mainPanel);
        this.panel.resizer = panelResizer;

        this.sidebar = new GuiDashboardSidebar(mc, this);
        this.sidebar.resizer = new Resizer().set(0.5F, 0, 32, 0.5F).setParent(this.area);
        this.sidebar.resizer.h.set(1, UnitMeasurement.PERCENTAGE);

        this.elements.add(this.panel);
        this.elements.add(this.sidebar);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}