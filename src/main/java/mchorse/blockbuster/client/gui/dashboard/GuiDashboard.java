package mchorse.blockbuster.client.gui.dashboard;

import mchorse.blockbuster.client.gui.dashboard.panels.GuiDashboardPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiMainPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.director.GuiDirectorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_block.GuiModelBlockPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiRecordingEditorPanel;
import mchorse.blockbuster.client.gui.elements.GuiMorphsPopup;
import mchorse.blockbuster.client.gui.framework.GuiBase;
import mchorse.blockbuster.client.gui.framework.elements.GuiDelegateElement;
import mchorse.blockbuster.client.gui.utils.Resizer;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDashboard extends GuiBase
{
    public static final ResourceLocation ICONS = new ResourceLocation("blockbuster", "textures/gui/dashboard/icons.png");

    public GuiDelegateElement panel;
    public GuiDespacito sidebar;

    public GuiDirectorPanel directorPanel;
    public GuiModelBlockPanel modelPanel;
    public GuiModelEditorPanel modelEditorPanel;
    public GuiRecordingEditorPanel recordingEditorPanel;
    public GuiMainPanel mainPanel;

    public GuiMorphsPopup morphs;

    private boolean mainMenu;

    public static void reset()
    {
        GuiModelBlockPanel.lastBlocks.clear();
        GuiDirectorPanel.lastBlocks.clear();
    }

    public GuiDashboard()
    {
        Minecraft mc = Minecraft.getMinecraft();

        this.modelEditorPanel = new GuiModelEditorPanel(mc, this);
        this.mainPanel = new GuiMainPanel(mc, this);
        this.createWorldPanels(mc);

        this.panel = new GuiDelegateElement(mc, this.mainPanel);
        this.panel.resizer().set(32, 0, 1, 1).parent(this.area).w(1F, -32).h(1F, 0);

        this.sidebar = new GuiDespacito(mc, this);
        this.sidebar.resizer = new Resizer().set(0.5F, 0, 32, 0.5F).parent(this.area).h(1F, 0);

        this.elements.add(this.panel);
        this.elements.add(this.sidebar);
    }

    private void createWorldPanels(Minecraft mc)
    {
        if (mc != null && mc.theWorld != null && this.directorPanel == null)
        {
            this.directorPanel = new GuiDirectorPanel(mc, this);
            this.modelPanel = new GuiModelBlockPanel(mc, this);
            this.recordingEditorPanel = new GuiRecordingEditorPanel(mc, this);
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
            this.recordingEditorPanel.close();
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

        return this.onOpen();
    }

    public GuiDashboard onOpen()
    {
        if (Minecraft.getMinecraft().theWorld != null)
        {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            IMorphing morphing = player == null ? null : Morphing.get(player);

            this.morphs = new GuiMorphsPopup(6, null, morphing);
            this.directorPanel.open();
            this.modelPanel.open();
            this.recordingEditorPanel.open();
        }

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