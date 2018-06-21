package mchorse.blockbuster.client.gui.dashboard;

import mchorse.blockbuster.client.gui.dashboard.panels.GuiDashboardPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiMainPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.director.GuiDirectorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_block.GuiModelPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.blockbuster.client.gui.framework.GuiBase;
import mchorse.blockbuster.client.gui.framework.elements.GuiDelegateElement;
import mchorse.blockbuster.client.gui.utils.Resizer;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDashboard extends GuiBase
{
    public static final ResourceLocation ICONS = new ResourceLocation("blockbuster", "textures/gui/dashboard/icons.png");

    public GuiDelegateElement panel;
    public GuiDashboardSidebar sidebar;

    public GuiDirectorPanel directorPanel;
    public GuiModelPanel modelPanel;
    public GuiModelEditorPanel modelEditorPanel;
    public GuiMainPanel mainPanel;

    private boolean mainMenu;

    public static void reset()
    {
        GuiModelPanel.lastBlocks.clear();
        GuiDirectorPanel.lastBlocks.clear();
    }

    public GuiDashboard()
    {
        Minecraft mc = Minecraft.getMinecraft();

        this.createWorldPanels(mc);
        this.modelEditorPanel = new GuiModelEditorPanel(mc);
        this.mainPanel = new GuiMainPanel(mc);

        this.panel = new GuiDelegateElement(mc, this.mainPanel);
        this.panel.resizer().set(32, 0, 1, 1).parent(this.area);
        this.panel.resizer().w.set(1, Measure.RELATIVE, -32);
        this.panel.resizer().h.set(1, Measure.RELATIVE);

        this.sidebar = new GuiDashboardSidebar(mc, this);
        this.sidebar.resizer = new Resizer().set(0.5F, 0, 32, 0.5F).parent(this.area);
        this.sidebar.resizer.h.set(1, Measure.RELATIVE);

        this.elements.add(this.panel);
        this.elements.add(this.sidebar);

        if (mc.world != null)
        {
            this.directorPanel.open();
            this.modelPanel.open();
        }
    }

    private void createWorldPanels(Minecraft mc)
    {
        if (mc != null && mc.world != null && this.directorPanel == null)
        {
            this.directorPanel = new GuiDirectorPanel(mc);
            this.modelPanel = new GuiModelPanel(mc);
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    protected void closeScreen()
    {
        /* Should I assume it's not null? :thonk: */
        if (!this.mainMenu)
        {
            this.directorPanel.close();
            this.modelPanel.close();
        }

        this.mc.displayGuiScreen(this.mainMenu ? new GuiMainMenu() : null);
    }

    public GuiDashboard setMainMenu(boolean main)
    {
        this.createWorldPanels(mc);
        this.mainMenu = main;

        return this;
    }

    public GuiDashboard open()
    {
        Minecraft.getMinecraft().displayGuiScreen(this);

        return this;
    }

    public GuiDashboard openPanel(GuiDashboardPanel element)
    {
        if (this.panel.delegate != null)
        {
            ((GuiDashboardPanel) this.panel.delegate).disappear();
        }

        element.appear();
        this.panel.setDelegate(element);

        return this;
    }

    @Override
    public void initGui()
    {
        this.sidebar.setVisible(!this.mainMenu);
        this.panel.resizer().w.padding = this.mainMenu ? 0 : -32;
        this.panel.resizer().x.value = this.mainMenu ? 0 : 32;

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.panel.delegate != null && ((GuiDashboardPanel) this.panel.delegate).needsBackground())
        {
            this.drawDefaultBackground();
        }
        else
        {
            this.drawGradientRect(0, 0, this.width, this.height / 8, 0x44000000, 0x00000000);
            this.drawGradientRect(0, this.height - this.height / 8, this.width, this.height, 0x00000000, 0x44000000);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}