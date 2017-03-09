package mchorse.blockbuster.model_editor;

import mchorse.blockbuster.client.gui.widgets.buttons.GuiTextureButton;
import mchorse.blockbuster.model_editor.elements.GuiLimbEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
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
}