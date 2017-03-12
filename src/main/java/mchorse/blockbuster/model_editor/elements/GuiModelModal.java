package mchorse.blockbuster.model_editor.elements;

import mchorse.blockbuster.model_editor.modal.GuiModal;
import mchorse.metamorph.api.models.Model;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

/**
 * Model properties modal
 *
 * This modal allows users to edit properties of the currently editing model
 * such as name, visual scale and texture size.
 */
public class GuiModelModal extends GuiModal
{
    public Model model;

    private int id;

    /* GUI fields */

    /**
     * Name of the model
     */
    public GuiTextField name;

    /**
     * Scale of the model
     */
    public GuiThreeInput scale;

    /**
     * Texture size of the model
     */
    public GuiTwoInput texture;

    /**
     * Button which activates saving of the model
     */
    private GuiButton done;

    public GuiModelModal(int id, GuiScreen parent, FontRenderer font)
    {
        super(parent, font);
        this.height = 140;
        this.id = id;
    }

    @Override
    public void initiate()
    {
        int x = this.parent.width / 2 + this.width / 2;
        int y = this.parent.height / 2 + this.height / 2;

        int w = this.width - 55;
        int x2 = x - 8 - w;

        this.name = new GuiTextField(0, this.font, x2 + 1, y - 104, w - 2, 18);
        this.scale = new GuiThreeInput(0, this.font, x2, y - 78, w, null);
        this.texture = new GuiTwoInput(0, this.font, x2 + 33, y - 53, w - 33, null);
        this.done = new GuiButton(this.id, x - this.buttonWidth - 8, y - 28, this.buttonWidth, 20, "Done");

        this.buttons.clear();
        this.buttons.add(this.done);

        if (this.model != null)
        {
            this.name.setText(this.model.name);

            this.scale.a.setText(String.valueOf(this.model.scale[0]));
            this.scale.b.setText(String.valueOf(this.model.scale[1]));
            this.scale.c.setText(String.valueOf(this.model.scale[2]));

            this.texture.a.setText(String.valueOf(this.model.texture[0]));
            this.texture.b.setText(String.valueOf(this.model.texture[1]));
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        this.name.textboxKeyTyped(typedChar, keyCode);
        this.scale.keyTyped(typedChar, keyCode);
        this.texture.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.name.mouseClicked(mouseX, mouseY, mouseButton);
        this.scale.mouseClicked(mouseX, mouseY, mouseButton);
        this.texture.mouseClicked(mouseX, mouseY, mouseButton);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawModal(int mouseX, int mouseY, float partialTicks)
    {
        super.drawModal(mouseX, mouseY, partialTicks);

        this.name.drawTextBox();
        this.scale.draw();
        this.texture.draw();

        int x = this.parent.width / 2 - this.width / 2 + 10;
        int y = this.parent.height / 2 + this.height / 2 - 9;

        this.font.drawStringWithShadow("Name", x, y - 90, 0xffffffff);
        this.font.drawStringWithShadow("Scale", x, y - 64, 0xffffffff);
        this.font.drawStringWithShadow("Texture size", x, y - 39, 0xffffffff);
    }
}