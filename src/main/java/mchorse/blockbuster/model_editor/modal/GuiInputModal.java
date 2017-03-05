package mchorse.blockbuster.model_editor.modal;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

/**
 * Input modal
 *
 * This modal is responsible for prompting the user to input some text data.
 * User will input some text, and then he will press proceed button.
 */
public class GuiInputModal extends GuiModal
{
    private GuiButton proceed;
    private GuiTextField input;

    public GuiInputModal(GuiScreen parent, FontRenderer font)
    {
        super(parent, font);
    }

    /**
     * Get user's input
     */
    public String getInput()
    {
        return this.input.getText();
    }

    @Override
    public void initButtons()
    {
        int x = this.parent.width / 2 - 90;
        int y = this.parent.height / 2 + 45 - 28;

        int w = 200 - 20;

        this.proceed = new GuiButton(-2, x + (w - this.buttonWidth), y, this.buttonWidth, 20, "Ok");
        this.input = new GuiTextField(-2, this.font, x, y + 2, w - 1 - this.buttonWidth, 16);

        this.buttons.clear();
        this.buttons.add(this.proceed);
    }

    /* Drawing, typing and clicking the text field */

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.input.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        this.input.textboxKeyTyped(typedChar, keyCode);

        /* On enter, submit */
        if (keyCode == 28)
        {
            this.actionPerformed(this.proceed);
        }
    }

    @Override
    public void drawModal(int mouseX, int mouseY, float partialTicks)
    {
        super.drawModal(mouseX, mouseY, partialTicks);
        this.input.drawTextBox();
    }
}