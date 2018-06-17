package mchorse.blockbuster.client.gui.framework.elements;

import java.util.function.Consumer;

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

        /* TODO: started dragging? */
        return false;
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
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.trackpad.draw(mouseX, mouseY, partialTicks);
    }
}