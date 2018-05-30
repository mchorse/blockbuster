package mchorse.blockbuster.client.gui.framework;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Delegated {@link GuiElement}
 */
@SideOnly(Side.CLIENT)
public class GuiDelegateElement extends GuiElement
{
    public GuiElement delegate;

    public GuiDelegateElement(GuiElement element)
    {
        this.delegate = element;
    }

    @Override
    public void resize(int width, int height)
    {
        if (this.delegate != null)
        {
            this.delegate.resize(width, height);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.delegate != null)
        {
            this.delegate.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void mouseScrolled(int mouseX, int mouseY, int scroll)
    {
        if (this.delegate != null)
        {
            this.delegate.mouseScrolled(mouseX, mouseY, scroll);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.delegate != null)
        {
            this.delegate.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public boolean hasActiveTextfields()
    {
        return this.delegate != null ? this.delegate.hasActiveTextfields() : false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        if (this.delegate != null)
        {
            this.delegate.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        if (this.delegate != null)
        {
            this.delegate.draw(mouseX, mouseY, partialTicks);
        }
    }
}