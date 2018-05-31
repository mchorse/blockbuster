package mchorse.blockbuster.client.gui.framework;

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
     * Whether this element is enabled (can handle any input) 
     */
    protected boolean enabled = true;

    /* Useful references */
    protected Minecraft mc;
    protected FontRenderer font;

    /**
     * Initiate GUI element with Minecraft's instance 
     */
    public GuiElement(Minecraft mc)
    {
        this.mc = mc;
        this.font = mc.fontRendererObj;
    }

    public GuiElement setResizer(Resizer resizer)
    {
        this.resizer = resizer;

        return this;
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
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
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {}

    @Override
    public void mouseScrolled(int mouseX, int mouseY, int scroll)
    {}

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
    public void draw(int mouseX, int mouseY, float partialTicks)
    {}
}