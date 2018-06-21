package mchorse.blockbuster.client;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.ModelPack;
import mchorse.blockbuster.client.gui.GuiActor;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiTextureButton;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.metamorph.client.gui.GuiCreativeMenu;
import mchorse.metamorph.client.gui.GuiSurvivalMenu;
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

/**
 * Main menu handler
 *
 * This handler is responsible for adding a button to model editor to main
 * menu. This implemented by listening to {@link InitGuiEvent} event for adding
 * the button, and checking whether button was clicked with
 * {@link ActionPerformedEvent}.
 */
public class MainMenuHandler
{
    public static final ResourceLocation TEXTURES = new ResourceLocation("blockbuster:textures/gui/model_editor.png");

    /**
     * Button in the main menu to get into model editor
     */
    public GuiTextureButton openModelEditor;

    public MainMenuHandler()
    {
        this.openModelEditor = new GuiTextureButton(-300, 5, 5, TEXTURES);
        this.openModelEditor.setTexPos(0, 0).setActiveTexPos(0, 16);
    }

    @SubscribeEvent
    public void onGuiInit(InitGuiEvent event)
    {
        GuiScreen screen = event.getGui();

        if (screen instanceof GuiMainMenu || screen instanceof GuiIngameMenu)
        {
            event.getButtonList().add(this.openModelEditor);
        }
    }

    @SubscribeEvent
    public void onActionPerformed(ActionPerformedEvent event)
    {
        GuiScreen screen = event.getGui();

        if (screen instanceof GuiMainMenu || screen instanceof GuiIngameMenu)
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

        boolean isMetamorph = gui instanceof GuiCreativeMenu || gui instanceof GuiSurvivalMenu;
        boolean isBlockbuster = gui instanceof GuiDashboard || gui instanceof GuiActor;
        boolean exitME = gui == null && Minecraft.getMinecraft().currentScreen instanceof GuiDashboard;

        if (isMetamorph || isBlockbuster || exitME)
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

            Blockbuster.proxy.loadModels(pack, false);
            ClientProxy.actorPack.pack.reload();
        }
    }
}