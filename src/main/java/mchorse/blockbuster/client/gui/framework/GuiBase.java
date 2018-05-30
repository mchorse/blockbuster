package mchorse.blockbuster.client.gui.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Base class for GUI screens using this framework
 */
@SideOnly(Side.CLIENT)
public class GuiBase extends GuiScreen
{
    public List<GuiElement> elements = new ArrayList<GuiElement>();

    @Override
    public void initGui()
    {
        for (GuiElement element : this.elements)
        {
            element.resize(this.width, this.height);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        for (GuiElement element : this.elements)
        {
            if (element.isEnabled())
            {
                element.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        for (GuiElement element : this.elements)
        {
            if (element.isEnabled())
            {
                element.mouseReleased(mouseX, mouseY, state);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        boolean anyActive = true;

        for (GuiElement element : this.elements)
        {
            if (element.isEnabled())
            {
                element.keyTyped(typedChar, keyCode);

                anyActive = anyActive && element.hasActiveTextfields();
            }
        }

        if (!anyActive)
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
        for (GuiElement element : this.elements)
        {
            element.draw(mouseX, mouseY, partialTicks);
        }
    }
}