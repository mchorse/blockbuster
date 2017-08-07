package mchorse.blockbuster.model_editor.elements;

import mchorse.blockbuster.model_editor.elements.GuiThreeInput.IMultiInputListener;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiTextField;

/**
 * Two input GUI widget
 *
 * This widget is responsible for managing two text fields.
 */
public class GuiTwoInput implements GuiResponder
{
    public GuiTextField a;
    public GuiTextField b;

    private int id;
    private IMultiInputListener listener;

    public GuiTwoInput(int id, FontRenderer font, int x, int y, int width, IMultiInputListener listener)
    {
        int w = (width - 4) / 2;

        this.id = id;
        this.listener = listener;

        this.a = new GuiTextField(id, font, x + 1, y + 1, w - 2, 16);
        this.b = new GuiTextField(id + 1, font, x + width - w, y + 1, w - 2, 16);

        this.a.setGuiResponder(this);
        this.b.setGuiResponder(this);
    }

    public void update(int x, int y, int width)
    {
        int w = (width - 4) / 2;

        this.a.x = x + 1;
        this.b.x = x + width - w + 1;

        this.a.y = this.b.y = y + 1;
        this.a.width = this.b.width = w - 2;
    }

    @Override
    public void setEntryValue(int id, boolean value)
    {}

    @Override
    public void setEntryValue(int id, float value)
    {}

    @Override
    public void setEntryValue(int id, String value)
    {
        if (this.listener != null)
        {
            this.listener.setValue(this.id, id - this.id, value);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.a.mouseClicked(mouseX, mouseY, mouseButton);
        this.b.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void keyTyped(char typedChar, int keyCode)
    {
        this.a.textboxKeyTyped(typedChar, keyCode);
        this.b.textboxKeyTyped(typedChar, keyCode);
    }

    public void draw()
    {
        this.a.drawTextBox();
        this.b.drawTextBox();
    }
}