package mchorse.blockbuster.client.gui.framework.elements;

import java.util.function.Consumer;

import mchorse.blockbuster.client.gui.widgets.buttons.GuiTextureButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiButtonElement<T extends GuiButton> extends GuiElement
{
    public T button;
    public Consumer<GuiButtonElement<T>> callback;

    public static GuiButtonElement<GuiCheckBox> checkbox(Minecraft mc, String label, boolean value, Consumer<GuiButtonElement<GuiCheckBox>> callback)
    {
        return new GuiButtonElement<GuiCheckBox>(mc, new GuiCheckBox(0, 0, 0, label, value), callback);
    }

    public static GuiButtonElement<GuiTextureButton> icon(Minecraft mc, ResourceLocation texture, int tx, int ty, int ax, int ay, Consumer<GuiButtonElement<GuiTextureButton>> callback)
    {
        return new GuiButtonElement<GuiTextureButton>(mc, new GuiTextureButton(0, 0, 0, texture).setTexPos(tx, ty).setActiveTexPos(ax, ay), callback);
    }

    public static GuiButtonElement<GuiButton> button(Minecraft mc, String label, Consumer<GuiButtonElement<GuiButton>> callback)
    {
        return new GuiButtonElement<GuiButton>(mc, new GuiButton(0, 0, 0, label), callback);
    }

    public GuiButtonElement(Minecraft mc, T button, Consumer<GuiButtonElement<T>> callback)
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
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        this.button.visible = visible;
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.button.x = this.area.x;
        this.button.y = this.area.y;
        this.button.width = this.area.w;
        this.button.height = this.area.h;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.button.mousePressed(this.mc, mouseX, mouseY))
        {
            this.button.playPressSound(this.mc.getSoundHandler());

            if (this.callback != null)
            {
                this.callback.accept(this);
            }

            return true;
        }

        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.button.mouseReleased(mouseX, mouseY);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.button.drawButton(this.mc, mouseX, mouseY, partialTicks);
    }
}