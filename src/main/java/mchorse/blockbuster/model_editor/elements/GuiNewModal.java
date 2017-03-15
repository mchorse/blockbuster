package mchorse.blockbuster.model_editor.elements;

import mchorse.blockbuster.model_editor.modal.GuiModal;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiNewModal extends GuiModal
{
    public GuiModelsView models;
    public GuiButton button;
    public int id;

    public GuiNewModal(int id, GuiScreen parent, FontRenderer font)
    {
        super(parent, font);
        this.models = new GuiModelsView(parent);
        this.height = 140;
        this.id = id;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.models.mouseClicked(mouseX, mouseY, mouseButton);
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
        int x = this.parent.width / 2 + this.width / 2;
        int y = this.parent.height / 2 + this.height / 2;

        this.models.updateRect(x - this.width + 11, y - this.height + 30, this.width - 22, this.height - 70);
        this.models.initiate();

        this.button = new GuiButton(this.id, x - this.width + 10, y - 41, this.width - 20, 20, "Done");
        this.buttons.clear();
        this.buttons.add(this.button);
    }

    @Override
    public void drawModal(int mouseX, int mouseY, float partialTicks)
    {
        super.drawModal(mouseX, mouseY, partialTicks);

        this.models.draw(mouseX, mouseY, partialTicks);

        if (this.models.selected != null)
        {
            String title = this.models.selected.key;
            int index = title.lastIndexOf(".");

            if (index != -1)
            {
                title = title.substring(index + 1);
            }

            this.parent.drawCenteredString(this.font, title, this.parent.width / 2, this.parent.height / 2 + this.height / 2 - 16, 0xffffff);
        }
    }
}