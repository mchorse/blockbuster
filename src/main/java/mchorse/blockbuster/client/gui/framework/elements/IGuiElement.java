package mchorse.blockbuster.client.gui.framework.elements;

import mchorse.blockbuster.client.gui.framework.GuiTooltip;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IGuiElement
{
    /**
     * Should be called by the screen when it was resized 
     */
    public void resize(int width, int height);

    /**
     * Whether this element is enabled (and can accept any input) 
     */
    public boolean isEnabled();

    /**
     * Whether this element is visible
     */
    public boolean isVisible();

    /**
     * Mouse was clicked
     */
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton);

    /**
     * Mouse wheel was scrolled
     */
    public boolean mouseScrolled(int mouseX, int mouseY, int scroll);

    /**
     * Mouse was released
     */
    public void mouseReleased(int mouseX, int mouseY, int state);

    /**
     * Does this element has any active text fields
     */
    public boolean hasActiveTextfields();

    /**
     * Key was typed
     */
    public void keyTyped(char typedChar, int keyCode);

    /**
     * Draw its components on the screen
     */
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks);
}