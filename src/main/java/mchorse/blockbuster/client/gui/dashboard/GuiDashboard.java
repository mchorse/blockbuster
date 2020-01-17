package mchorse.blockbuster.client.gui.dashboard;

import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiDashboardPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiMainPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiTextureManagerPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.director.GuiDirectorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_block.GuiModelBlockPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiRecordingEditorPanel;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.utils.Resizer;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphsMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Blockbuster's dashboard GUI entry
 */
@SideOnly(Side.CLIENT)
public class GuiDashboard extends GuiBase
{
    /**
     * Icons texture used across all dashboard panels 
     */
    public static final ResourceLocation GUI_ICONS = new ResourceLocation("blockbuster", "textures/gui/dashboard/icons.png");

    public GuiDelegateElement<GuiDashboardPanel> panel;
    public GuiDespacito sidebar;

    public GuiDirectorPanel directorPanel;
    public GuiModelBlockPanel modelPanel;
    public GuiModelEditorPanel modelEditorPanel;
    public GuiRecordingEditorPanel recordingEditorPanel;
    public GuiTextureManagerPanel texturePanel;
    public GuiMainPanel mainPanel;

    public GuiCreativeMorphsMenu morphs;
    public GuiDelegateElement<GuiCreativeMorphsMenu> morphDelegate;

    private boolean mainMenu;

    public static void reset()
    {
        GuiModelBlockPanel.lastBlocks.clear();
        GuiDirectorPanel.lastBlocks.clear();
    }

    public GuiDashboard()
    {
        Minecraft mc = Minecraft.getMinecraft();

        this.mc = mc;
        this.modelEditorPanel = new GuiModelEditorPanel(mc, this);
        this.mainPanel = new GuiMainPanel(mc, this);
        this.texturePanel = new GuiTextureManagerPanel(mc, this);

        this.panel = new GuiDelegateElement<GuiDashboardPanel>(mc, this.mainPanel);
        this.panel.resizer().set(32, 0, 1, 1).parent(this.area).w(1F, -32).h(1F, 0);

        this.sidebar = new GuiDespacito(mc, this);
        this.sidebar.resizer = new Resizer().set(0.5F, 0, 32, 0.5F).parent(this.area).h(1F, 0);

        this.elements.add(this.panel);
        this.elements.add(this.sidebar);
    }

    public GuiDashboard setMainMenu(boolean main)
    {
        this.mainMenu = main;

        return this;
    }

    public void createWorldPanels(Minecraft mc, boolean reloadCamera)
    {
        if (mc != null && mc.theWorld != null && this.directorPanel == null)
        {
            this.morphDelegate = new GuiDelegateElement<GuiCreativeMorphsMenu>(mc, null);
            this.directorPanel = new GuiDirectorPanel(mc, this);
            this.modelPanel = new GuiModelBlockPanel(mc, this);
            this.recordingEditorPanel = new GuiRecordingEditorPanel(mc, this);

            if (reloadCamera && CameraHandler.isApertureLoaded())
            {
                CameraHandler.reloadCameraEditor();
            }
        }
    }

    public GuiDashboard open()
    {
        this.createWorldPanels(this.mc, true);
        this.onOpen();

        Minecraft.getMinecraft().displayGuiScreen(this);

        return this;
    }

    public GuiDashboard onOpen()
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld != null)
        {
            EntityPlayer player = mc.thePlayer;
            IMorphing morphing = player == null ? null : Morphing.get(player);

            this.morphs = new GuiCreativeMorphsMenu(mc, 6, null, morphing);
            this.directorPanel.open();
            this.modelPanel.open();
            this.recordingEditorPanel.open();
            this.morphDelegate.setDelegate(this.morphs);
        }

        this.modelEditorPanel.open();
        this.texturePanel.open();

        return this;
    }

    public GuiDashboard openPanel(GuiDashboardPanel element)
    {
        if (element == null)
        {
            element = this.panel.delegate;
        }

        if (this.morphs != null)
        {
            this.morphs.callback = null;
            this.morphs.setVisible(false);
            this.morphs.setFilter("");
            this.morphs.setSelected(null);
        }

        if (this.panel.delegate != null)
        {
            this.panel.delegate.disappear();
        }

        this.panel.setDelegate(element);
        element.appear();

        return this;
    }

    /**
     * Clears the world dependent panels and morph picker by setting 
     * them to null 
     */
    public void clear()
    {
        this.morphs = null;
        this.morphDelegate = null;
        this.directorPanel = null;
        this.modelPanel = null;
        this.recordingEditorPanel = null;

        this.panel.setDelegate(this.mainPanel);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    public void close()
    {
        this.closeScreen();
    }

    @Override
    protected void closeScreen()
    {
        this.panel.delegate.disappear();

        if (!this.mainMenu)
        {
            this.directorPanel.close();
            this.modelPanel.close();
            this.recordingEditorPanel.close();
            this.mainPanel.close();
        }

        this.texturePanel.close();

        this.mc.displayGuiScreen(this.mainMenu ? new GuiMainMenu() : null);
    }

    @Override
    public void initGui()
    {
        /* If this GUI was opened in main menu, then the sidebar should 
         * be hidden */
        this.sidebar.setVisible(!this.mainMenu);
        this.panel.resizer().w.padding = this.mainMenu ? 0 : -32;
        this.panel.resizer().x.value = this.mainMenu ? 0 : 32;

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.panel.delegate != null && this.panel.delegate.needsBackground())
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