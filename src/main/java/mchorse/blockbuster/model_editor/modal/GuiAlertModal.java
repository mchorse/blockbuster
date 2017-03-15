package mchorse.blockbuster.model_editor.modal;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * Alert modal
 *
 * This modal is responsible for notifying the user about status of something.
 * User can close this modal by pressing the cancel button.
 */
public class GuiAlertModal extends GuiModal
{
    private GuiButton cancel;
    private int id;

    public GuiAlertModal(int id, GuiScreen parent, FontRenderer font)
    {
        super(parent, font);
        this.id = id;
    }

    @Override
    public void initiate()
    {
        int x = this.parent.width / 2 + this.width / 2;
        int y = this.parent.height / 2 + this.height / 2;

        this.cancel = new GuiButton(this.id, x - this.buttonWidth - 8, y - 28, this.buttonWidth, 20, "Ok");

        this.buttons.clear();
        this.buttons.add(this.cancel);
    }
}