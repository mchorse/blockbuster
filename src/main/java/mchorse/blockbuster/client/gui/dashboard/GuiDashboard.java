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

    public static void reset()
    {
        GuiModelPanel.lastBlocks.clear();
        GuiDirectorPanel.lastBlocks.clear();
    }

    public GuiDashboard()
    {
        Minecraft mc = Minecraft.getMinecraft();
        Resizer panelResizer = new Resizer().set(32, 0, 1, 1).parent(this.area);
        panelResizer.w.set(1, Measure.RELATIVE, -32);
        panelResizer.h.set(1, Measure.RELATIVE);

        this.directorPanel = new GuiDirectorPanel(mc);
        this.modelPanel = new GuiModelPanel(mc);
        this.modelEditorPanel = new GuiModelEditorPanel(mc);
        this.mainPanel = new GuiMainPanel(mc);

        this.panel = new GuiDelegateElement(mc, this.mainPanel);
        this.panel.resizer = panelResizer;

        this.sidebar = new GuiDashboardSidebar(mc, this);
        this.sidebar.resizer = new Resizer().set(0.5F, 0, 32, 0.5F).parent(this.area);
        this.sidebar.resizer.h.set(1, Measure.RELATIVE);

        this.elements.add(this.panel);
        this.elements.add(this.sidebar);

        if (mc.theWorld != null)
        {
            this.directorPanel.open();
            this.modelPanel.open();
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
        this.directorPanel.close();
        this.modelPanel.close();

        super.closeScreen();
    }

    public void openPanel(GuiDashboardPanel element)
    {
        if (this.panel.delegate != null)
        {
            ((GuiDashboardPanel) this.panel.delegate).disappear();
        }

        this.panel.delegate = element;
        element.appear();

        if (this.width != 0 && this.height != 0)
        {
            this.panel.resize(this.width, this.height);
        }
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