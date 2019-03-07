package mchorse.blockbuster.client.gui;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.api.ModelPack;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.model.parsing.ModelExtrudedLayer;
import mchorse.mclib.client.gui.widgets.buttons.GuiTextureButton;
import mchorse.metamorph.api.events.ReloadMorphs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Main menu handler
 */
@SideOnly(Side.CLIENT)
public class MenuHandler
{
    public static final ResourceLocation TEXTURES = new ResourceLocation("blockbuster:textures/gui/model_editor.png");

    /**    
    * Button in the main menu to get into model editor 
    */
    public GuiTextureButton openModelEditor;

    public MenuHandler()
    {
        this.openModelEditor = new GuiTextureButton(-300, 5, 5, TEXTURES);
        this.openModelEditor.setTexPos(0, 0).setActiveTexPos(0, 16);
    }

    @SubscribeEvent
    public void onGuiInit(InitGuiEvent event)
    {
        GuiScreen screen = event.getGui();

        if (screen instanceof GuiIngameMenu)
        {
            event.getButtonList().add(this.openModelEditor);
        }
    }

    @SubscribeEvent
    public void onActionPerformed(ActionPerformedEvent event)
    {
        GuiScreen screen = event.getGui();

        if (screen instanceof GuiIngameMenu)
        {
            if (event.getButton() == this.openModelEditor)
            {
                GuiDashboard dashboard = ClientProxy.getDashboard(screen instanceof GuiMainMenu);

                dashboard.open().openPanel(dashboard.modelEditorPanel);
            }
        }
    }

    /**
     * Refresh models, skins and morphs upon entering in Metamorph and/or
     * Blockbuster GUIs.
     */
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event)
    {
        if (!Blockbuster.proxy.config.auto_refresh_models)
        {
            return;
        }

        GuiScreen gui = event.getGui();

        if (gui instanceof GuiMainMenu)
        {
            ModelExtrudedLayer.clear();
            ClientProxy.getDashboard(true).clear();
        }
    }

    /**
     * On morphs creative picker compilation, reload all morphs 
     */
    @SubscribeEvent
    public void onMorphsReload(ReloadMorphs event)
    {
        /* Reload models and skin */
        ModelPack pack = Blockbuster.proxy.models.pack;

        if (pack == null)
        {
            pack = Blockbuster.proxy.getPack();

            if (Minecraft.getMinecraft().isSingleplayer())
            {
                pack.addFolder(DimensionManager.getCurrentSaveRootDirectory() + "/blockbuster/models");
            }
        }

        ClientProxy.actorPack.pack.reload();
        Blockbuster.proxy.loadModels(pack, false);
    }
}