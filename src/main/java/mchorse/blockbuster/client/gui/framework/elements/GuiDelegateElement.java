package mchorse.blockbuster.client.gui.framework.elements;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Delegated {@link IGuiElement}
 */
@SideOnly(Side.CLIENT)
public class GuiDelegateElement extends GuiElement implements IGuiLegacy
{
    public GuiElement delegate;

    public GuiDelegateElement(Minecraft mc, GuiElement element)
    {
        super(mc);
        this.delegate = element;
    }

    @Override
    public void resize(int width, int height)
    {
        if (this.delegate != null)
        {
            this.delegate.resizer = this.resizer;
            this.delegate.resize(width, height);
        }
    }

    @Override
    public boolean handleMouseInput(int mouseX, int mouseY) throws IOException
    {
        if (this.delegate instanceof IGuiLegacy)
        {
            return ((IGuiLegacy) this.delegate).handleMouseInput(mouseX, mouseY);
        }

        return false;
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
    public boolean handleKeyboardInput() throws IOException
    {
        if (this.delegate instanceof IGuiLegacy)
        {
            return ((IGuiLegacy) this.delegate).handleKeyboardInput();
        }

        return false;
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