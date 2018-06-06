package mchorse.blockbuster.client.gui.framework.elements;

import mchorse.blockbuster.client.gui.utils.Area;
import mchorse.blockbuster.client.gui.utils.Resizer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiElement extends Gui implements IGuiElement
{
    /**
     * Area of this element (i.e. position and size) 
     */
    public Area area = new Area();

    /**
     * Resizer of this class
     */
    public Resizer resizer;

    /**
     * Children elements
     */
    public GuiElements children;

    /**
     * Whether this element is enabled (can handle any input) 
     */
    protected boolean enabled = true;

    /**
     * Whether this element is visible 
     */
    protected boolean visible = true;

    /* Useful references */
    protected Minecraft mc;
    protected FontRenderer font;

    /**
     * Initiate GUI element with Minecraft's instance 
     */
    public GuiElement(Minecraft mc)
    {
        this.mc = mc;
        this.font = mc.fontRenderer;
    }

    public GuiElement createChildren()
    {
        this.children = new GuiElements();

        return this;
    }

    /* Resizer methods */

    public Resizer resizer()
    {
        if (this.resizer == null)
        {
            this.resizer = new Resizer();
        }

        return this.resizer;
    }

    public GuiElement setResizer(Resizer resizer)
    {
        this.resizer = resizer;

        return this;
    }

    /* Enabled methods */

    @Override
    public boolean isEnabled()
    {
        return this.enabled && this.visible;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public boolean isVisible()
    {
        return this.visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    /* Overriding those methods so it would be much easier to 
     * override only needed methods in subclasses */

    @Override
    public void resize(int width, int height)
    {
        if (this.resizer != null)
        {
            this.resizer.apply(this.area);
        }

        if (this.children != null)
        {
            this.children.resize(width, height);
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.children != null)
        {
            return this.children.mouseClicked(mouseX, mouseY, mouseButton);
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int scroll)
    {
        if (this.children != null)
        {
            return this.children.mouseScrolled(mouseX, mouseY, scroll);
        }

        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.children != null)
        {
            this.children.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public boolean hasActiveTextfields()
    {
        if (this.children != null)
        {
            return this.children.hasActiveTextfields();
        }

        return false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        if (this.children != null)
        {
            this.children.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        if (this.children != null)
        {
            this.children.draw(mouseX, mouseY, partialTicks);
        }
    }
}