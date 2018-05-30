package mchorse.blockbuster.client.gui.framework;

import mchorse.blockbuster.utils.Area;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiElement implements IGuiElement
{
    /**
     * Area of this element (i.e. position and size) 
     */
    public Area area = new Area();

    /**
     * Whether this element is enabled 
     */
    protected boolean enabled = true;

    public void setArea(int x, int y, int w, int h)
    {
        this.area.set(x, y, w, h);
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}