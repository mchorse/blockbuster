package mchorse.blockbuster.client.gui.framework.elements;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiTextField;

/**
 * GUI text element
 * 
 * This element is a wrapper for the text field class
 */
public class GuiTextElement extends GuiElement implements GuiResponder
{
    public GuiTextField field;
    public Consumer<GuiTextElement> callback;

    public GuiTextElement(Minecraft mc, Consumer<GuiTextElement> callback)
    {
        super(mc);

        this.field = new GuiTextField(0, this.font, 0, 0, 0, 0);
        this.callback = callback;
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
        if (this.callback != null)
        {
            this.callback.accept(this);
        }
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);

        this.field.setEnabled(enabled);
    }

    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);

        this.field.setVisible(visible);
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.field.xPosition = this.area.x;
        this.field.yPosition = this.area.y;
        this.field.width = this.area.w;
        this.field.height = this.area.h;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (super.mouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        boolean wasFocused = this.field.isFocused();

        this.field.mouseClicked(mouseX, mouseY, mouseButton);

        if (wasFocused != this.field.isFocused())
        {
            return true;
        }

        return false;
    }

    @Override
    public boolean hasActiveTextfields()
    {
        return this.field.isFocused();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        super.keyTyped(typedChar, keyCode);

        this.field.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.field.drawTextBox();

        super.draw(mouseX, mouseY, partialTicks);
    }
}