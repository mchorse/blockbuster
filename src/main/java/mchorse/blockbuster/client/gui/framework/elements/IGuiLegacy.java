package mchorse.blockbuster.client.gui.framework.elements;

import java.io.IOException;

/**
 * Interface for legacy support of Minecraft events 
 */
public interface IGuiLegacy
{
    public boolean handleMouseInput(int mouseX, int mouseY) throws IOException;

    public boolean handleKeyboardInput() throws IOException;
}