package mchorse.blockbuster.model_editor;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.ModelPack;
import mchorse.blockbuster.client.gui.GuiActor;
import mchorse.blockbuster.client.gui.GuiDirector;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiTextureButton;
import mchorse.blockbuster.model_editor.elements.GuiLimbEditor;
import mchorse.metamorph.client.gui.GuiCreativeMenu;
import mchorse.metamorph.client.gui.GuiSurvivalMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
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
    /**
     * Button in the main menu to get into model editor
     */
    public GuiTextureButton openModelEditor;

    public MainMenuHandler()
    {
        this.openModelEditor = new GuiTextureButton(-300, 5, 5, GuiLimbEditor.GUI);
        this.openModelEditor.setTexPos(0, 0).setActiveTexPos(0, 16);
    }

    @SubscribeEvent
    public void onGuiInit(InitGuiEvent event)
    {
        if (event.getGui() instanceof GuiMainMenu)
        {
            event.getButtonList().add(this.openModelEditor);
        }
    }

    @SubscribeEvent
    public void onActionPerformed(ActionPerformedEvent event)
    {
        if (event.getGui() instanceof GuiMainMenu && event.getButton() == this.openModelEditor)
        {
            Minecraft.getMinecraft().displayGuiScreen(new GuiModelEditor(true));
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event)
    {
        GuiScreen gui = event.getGui();

        boolean isMetamorph = gui instanceof GuiCreativeMenu || gui instanceof GuiSurvivalMenu;
        boolean isBlockbuster = gui instanceof GuiDirector || gui instanceof GuiActor || gui instanceof GuiModelEditor;

        if (isMetamorph || isBlockbuster)
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

            Blockbuster.proxy.loadModels(pack);
        }
    }
}