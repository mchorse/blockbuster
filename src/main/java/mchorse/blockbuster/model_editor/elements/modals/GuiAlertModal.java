package mchorse.blockbuster.model_editor.elements.modals;

import mchorse.blockbuster.model_editor.modal.GuiModal;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

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
        super.initiate();

        int x = this.x + this.width - this.buttonWidth - 8;
        int y = this.y + this.height - 28;

        this.cancel = new GuiButton(this.id, x, y, this.buttonWidth, 20, I18n.format("blockbuster.gui.ok"));

        this.buttons.clear();
        this.buttons.add(this.cancel);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        /* On enter, submit */
        if (keyCode == 28)
        {
            this.actionPerformed(this.cancel);
        }
    }
}