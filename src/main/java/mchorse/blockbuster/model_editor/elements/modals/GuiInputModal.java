package mchorse.blockbuster.model_editor.elements.modals;

import mchorse.blockbuster.model_editor.modal.GuiModal;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

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
    private int id;
    private String inputText = "";

    public GuiInputModal(int id, GuiScreen parent, FontRenderer font)
    {
        super(parent, font);
        this.id = id;
    }

    /**
     * Get user's input
     */
    public String getInput()
    {
        return this.input.getText();
    }

    /**
     * Set user's input
     */
    public GuiInputModal setInput(String input)
    {
        this.inputText = input;

        if (this.input != null)
        {
            this.input.setText(input);
        }

        return this;
    }

    @Override
    public void initiate()
    {
        super.initiate();

        int w = this.width - this.buttonWidth - 25;
        int y = this.y + this.height - 30;

        this.proceed = new GuiButton(this.id, this.x + w + 15, y, this.buttonWidth, 20, I18n.format("blockbuster.gui.ok"));
        this.input = new GuiTextField(-2, this.font, this.x + 11, y + 1, w - 2, 18);
        this.input.setText(this.inputText);

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