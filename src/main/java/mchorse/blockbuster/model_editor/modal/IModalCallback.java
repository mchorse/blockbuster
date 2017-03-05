package mchorse.blockbuster.model_editor.modal;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * Modal callback interface
 *
 * This interface is responsible for providing parent GUI screen of the modal
 * an ability to communicate when a button pressed. Just like {@link GuiScreen}'s
 * actionPerfomed method.
 */
public interface IModalCallback
{
    public void modalButtonPressed(GuiModal modal, GuiButton button);
}