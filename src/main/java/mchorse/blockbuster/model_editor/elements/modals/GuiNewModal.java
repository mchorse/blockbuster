package mchorse.blockbuster.model_editor.elements.modals;

import mchorse.blockbuster.model_editor.GuiModelEditor;
import mchorse.blockbuster.model_editor.elements.scrolls.GuiModelsView;
import mchorse.blockbuster.model_editor.modal.GuiModal;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

/**
 * New/edit model GUI modal
 *
 * This GUI is responsible for showing available models for editing/using as a
 * template, searching and selecting them.
 */
public class GuiNewModal extends GuiModal
{
    private final String strSearch = I18n.format("blockbuster.gui.me.search");

    public GuiModelsView models;
    public GuiButton button;
    public GuiTextField search;
    public int id;

    public GuiNewModal(int id, GuiModelEditor parent, FontRenderer font)
    {
        super(parent, font);
        this.models = new GuiModelsView(parent);
        this.height = 190;
        this.id = id;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        super.keyTyped(typedChar, keyCode);
        this.search.textboxKeyTyped(typedChar, keyCode);

        if (this.search.isFocused())
        {
            this.models.search(this.search.getText());
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.models.mouseClicked(mouseX, mouseY, mouseButton);
        this.search.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
        this.models.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void wheelScroll(int mouseX, int mouseY, int scroll)
    {
        this.models.scrollMouse(scroll, mouseX, mouseY);
    }

    @Override
    public void initiate()
    {
        super.initiate();

        int x = this.x + this.width;
        int y = this.y + this.height;

        this.models.updateRect(this.x + 11, this.y + 50, this.width - 22, this.height - 90);
        this.models.initiate();

        this.search = new GuiTextField(0, this.font, this.parent.width / 2 - this.width / 2 + 12, this.parent.height / 2 - this.height / 2 + 32, this.width - 24, 18);

        this.button = new GuiButton(this.id, x - this.width + 10, y - 41, this.width - 20, 20, I18n.format("blockbuster.gui.done"));
        this.buttons.clear();
        this.buttons.add(this.button);
    }

    @Override
    public void drawModal(int mouseX, int mouseY, float partialTicks)
    {
        super.drawModal(mouseX, mouseY, partialTicks);

        this.models.draw(mouseX, mouseY, partialTicks);
        this.search.drawTextBox();

        if (!this.search.isFocused() && this.search.getText().isEmpty())
        {
            this.font.drawStringWithShadow(this.strSearch, this.search.xPosition + 4, this.search.yPosition + 5, 0xaaaaaa);
        }

        if (this.models.selected != null)
        {
            this.parent.drawCenteredString(this.font, this.models.selected.name, this.parent.width / 2, this.parent.height / 2 + this.height / 2 - 16, 0xffffff);
        }
    }
}