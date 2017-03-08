package mchorse.blockbuster.model_editor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
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
    public GuiButton openModelEditor;

    public MainMenuHandler()
    {
        this.openModelEditor = new GuiButton(-300, 10, 10, 80, 20, "Model Editor");
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