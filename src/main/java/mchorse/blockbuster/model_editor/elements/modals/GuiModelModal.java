package mchorse.blockbuster.model_editor.elements.modals;

import mchorse.blockbuster.model_editor.elements.GuiThreeInput;
import mchorse.blockbuster.model_editor.elements.GuiTwoInput;
import mchorse.blockbuster.model_editor.modal.GuiModal;
import mchorse.metamorph.api.models.Model;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiCheckBox;

/**
 * Model properties modal
 *
 * This modal allows users to edit properties of the currently editing model
 * such as name, visual scale and texture size.
 */
public class GuiModelModal extends GuiModal
{
    private final String strName = I18n.format("blockbuster.gui.me.model.name");
    private final String strScale = I18n.format("blockbuster.gui.me.model.scale");
    private final String strTextureSize = I18n.format("blockbuster.gui.me.model.texture_size");
    private final String strTexture = I18n.format("blockbuster.gui.me.model.texture");

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
    public GuiTwoInput textureSize;

    /**
     * Path to default texture of the model
     */
    public GuiTextField texture;

    /**
     * Whether this model provides OBJ model 
     */
    public GuiCheckBox providesObj;

    /**
     * Button which activates saving of the model
     */
    private GuiButton done;

    public GuiModelModal(int id, GuiScreen parent, FontRenderer font)
    {
        super(parent, font);
        this.height = 200;
        this.id = id;
    }

    public GuiModelModal setModel(Model model)
    {
        this.model = model;

        return this;
    }

    @Override
    public void initiate()
    {
        super.initiate();

        int x = this.x + this.width;
        int y = this.y + this.height;

        int w = this.width;

        /* Some dirty way to make things dynamically adjust or so */
        y -= 30;
        this.done = new GuiButton(this.id, x - this.buttonWidth - 10, y, this.buttonWidth, 20, I18n.format("blockbuster.gui.done"));
        y -= 25;
        this.providesObj = new GuiCheckBox(0, x, y, I18n.format("blockbuster.gui.me.model.provides_obj"), false);
        this.providesObj.x -= this.providesObj.width + 12;
        y -= 25;
        w = this.width - this.font.getStringWidth(this.strTexture) - 24;
        this.texture = new GuiTextField(0, this.font, x - 10 - w + 1, y, w - 3, 18);
        y -= 25;
        w = this.width - this.font.getStringWidth(this.strTextureSize) - 24;
        this.textureSize = new GuiTwoInput(0, this.font, x - 10 - w, y, w, null);
        y -= 25;
        w = this.width - this.font.getStringWidth(this.strScale) - 24;
        this.scale = new GuiThreeInput(0, this.font, x - 10 - w, y, w, null);
        y -= 25;
        w = this.width - this.font.getStringWidth(this.strName) - 24;
        this.name = new GuiTextField(0, this.font, x - 10 - w + 1, y, w - 2, 18);

        this.buttons.clear();
        this.buttons.add(this.done);

        if (this.model != null)
        {
            this.name.setText(this.model.name);

            this.scale.a.setText(String.valueOf(this.model.scale[0]));
            this.scale.b.setText(String.valueOf(this.model.scale[1]));
            this.scale.c.setText(String.valueOf(this.model.scale[2]));

            this.textureSize.a.setText(String.valueOf(this.model.texture[0]));
            this.textureSize.b.setText(String.valueOf(this.model.texture[1]));

            this.texture.setMaxStringLength(200);

            if (this.model.defaultTexture != null)
            {
                this.texture.setText(this.model.defaultTexture.toString());
            }

            this.texture.setCursorPosition(0);
            this.providesObj.setIsChecked(this.model.providesObj);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        this.name.textboxKeyTyped(typedChar, keyCode);
        this.scale.keyTyped(typedChar, keyCode);
        this.textureSize.keyTyped(typedChar, keyCode);
        this.texture.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.name.mouseClicked(mouseX, mouseY, mouseButton);
        this.scale.mouseClicked(mouseX, mouseY, mouseButton);
        this.textureSize.mouseClicked(mouseX, mouseY, mouseButton);
        this.texture.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.providesObj.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY))
        {
            this.model.providesObj = this.providesObj.isChecked();
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawModal(int mouseX, int mouseY, float partialTicks)
    {
        super.drawModal(mouseX, mouseY, partialTicks);

        this.name.drawTextBox();
        this.scale.draw();
        this.textureSize.draw();
        this.texture.drawTextBox();
        this.providesObj.drawButton(Minecraft.getMinecraft(), mouseX, mouseY, partialTicks);

        int x = this.x + 10;
        int y = this.y + this.height - 75;

        this.font.drawStringWithShadow(this.strTexture, x, y, 0xffffffff);
        y -= 25;
        this.font.drawStringWithShadow(this.strTextureSize, x, y, 0xffffffff);
        y -= 25;
        this.font.drawStringWithShadow(this.strScale, x, y, 0xffffffff);
        y -= 25;
        this.font.drawStringWithShadow(this.strName, x, y, 0xffffffff);
    }
}