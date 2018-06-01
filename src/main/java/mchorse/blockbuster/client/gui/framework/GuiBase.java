package mchorse.blockbuster.client.gui.framework;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import mchorse.blockbuster.client.gui.framework.elements.GuiElements;
import mchorse.blockbuster.client.gui.utils.Area;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Base class for GUI screens using this framework
 */
@SideOnly(Side.CLIENT)
public class GuiBase extends GuiScreen
{
    public GuiElements elements = new GuiElements();
    public Area area = new Area();

    @Override
    public void initGui()
    {
        this.area.set(0, 0, this.width, this.height);
        this.elements.resize(this.width, this.height);
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();

        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int scroll = -Mouse.getEventDWheel();

        if (scroll == 0)
        {
            return;
        }

        this.elements.mouseScrolled(x, y, scroll);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        this.elements.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.elements.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        this.elements.keyTyped(typedChar, keyCode);

        if (!this.elements.hasActiveTextfields())
        {
            this.keyPressed(typedChar, keyCode);
        }

        super.keyTyped(typedChar, keyCode);
    }

    /**
     * This method is getting called when there are no active text 
     * fields in the GUI (this can be used for handling shortcuts)
     */
    public void keyPressed(char typedChar, int keyCode)
    {}

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.elements.draw(mouseX, mouseY, partialTicks);
    }
}