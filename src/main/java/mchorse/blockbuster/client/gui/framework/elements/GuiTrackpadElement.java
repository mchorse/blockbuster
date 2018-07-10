package mchorse.blockbuster.client.gui.framework.elements;

import java.util.function.Consumer;

import mchorse.blockbuster.client.gui.framework.GuiTooltip;
import mchorse.blockbuster.client.gui.widgets.GuiTrackpad;
import mchorse.blockbuster.client.gui.widgets.GuiTrackpad.ITrackpadListener;
import net.minecraft.client.Minecraft;

public class GuiTrackpadElement extends GuiElement implements ITrackpadListener
{
    public GuiTrackpad trackpad;
    public Consumer<Float> callback;

    public GuiTrackpadElement(Minecraft mc, String label, Consumer<Float> callback)
    {
        super(mc);

        this.trackpad = new GuiTrackpad(this, this.font);
        this.trackpad.setTitle(label);
        this.callback = callback;
    }

    @Override
    public void setTrackpadValue(GuiTrackpad trackpad, float value)
    {
        if (this.callback != null)
        {
            this.callback.accept(value);
        }
    }

    public void setLimit(float min, float max)
    {
        this.trackpad.min = min;
        this.trackpad.max = max;
    }

    public void setLimit(float min, float max, boolean integer)
    {
        this.setLimit(min, max);
        this.trackpad.integer = true;
    }

    public void setValue(float value)
    {
        this.trackpad.setValue(value);
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.trackpad.update(this.area.x, this.area.y, this.area.w, this.area.h);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.trackpad.mouseClicked(mouseX, mouseY, mouseButton);

        return this.trackpad.isDragging();
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.trackpad.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        this.trackpad.keyTyped(typedChar, keyCode);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        this.trackpad.draw(mouseX, mouseY, partialTicks);
    }
}