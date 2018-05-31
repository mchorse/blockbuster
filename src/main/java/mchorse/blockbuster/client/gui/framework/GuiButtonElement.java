package mchorse.blockbuster.client.gui.framework;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiButtonElement extends GuiElement
{
    public GuiButton button;
    public Consumer<GuiButtonElement> callback;

    public GuiButtonElement(Minecraft mc, String label, Consumer<GuiButtonElement> callback)
    {
        this(mc, new GuiButton(0, 0, 0, label), callback);
    }

    public GuiButtonElement(Minecraft mc, GuiButton button, Consumer<GuiButtonElement> callback)
    {
        super(mc);
        this.button = button;
        this.callback = callback;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        this.button.enabled = enabled;
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.button.xPosition = this.area.x;
        this.button.yPosition = this.area.y;
        this.button.width = this.area.w;
        this.button.height = this.area.h;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.button.mousePressed(this.mc, mouseX, mouseY) && this.callback != null)
        {
            this.button.playPressSound(this.mc.getSoundHandler());
            this.callback.accept(this);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.button.mouseReleased(mouseX, mouseY);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.button.drawButton(this.mc, mouseX, mouseY);
    }
}