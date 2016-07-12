package noname.blockbuster.client.gui.elements;

import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Custom implementation of the button
 *
 * This class extends GuiButton and allows buttons created with this class
 * could store generic data for later retrieval.
 *
 * @author mchorse
 */
@SideOnly(Side.CLIENT)
public class GuiCustomButton<T> extends GuiButton
{
    protected T value;

    public GuiCustomButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText)
    {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    public void setValue(T value)
    {
        this.value = value;
    }

    public T getValue()
    {
        return this.value;
    }
}
