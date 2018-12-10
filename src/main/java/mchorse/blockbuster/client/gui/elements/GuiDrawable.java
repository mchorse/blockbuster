package mchorse.blockbuster.client.gui.elements;

import java.util.function.Consumer;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;

public class GuiDrawable implements IGuiElement
{
    public Consumer<Void> callback;

    public GuiDrawable(Consumer<Void> callback)
    {
        this.callback = callback;
    }

    @Override
    public void resize(int width, int height)
    {}

    @Override
    public boolean isEnabled()
    {
        return false;
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        return false;
    }

    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int scroll)
    {
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {}

    @Override
    public boolean hasActiveTextfields()
    {
        return false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {}

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        if (this.callback != null)
        {
            this.callback.accept(null);
        }
    }
}