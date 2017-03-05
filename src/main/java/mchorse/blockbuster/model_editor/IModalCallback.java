package mchorse.blockbuster.model_editor;

import mchorse.blockbuster.model_editor.modal.GuiModal;
import net.minecraft.client.gui.GuiButton;

public interface IModalCallback
{
    public void modalButtonPressed(GuiModal modal, GuiButton button);
}