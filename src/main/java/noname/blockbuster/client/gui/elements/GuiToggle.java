package noname.blockbuster.client.gui.elements;

import net.minecraft.client.gui.GuiButton;

public class GuiToggle extends GuiButton
{
    public String onText;
    public String offText;
    protected boolean value = false;

    public GuiToggle(int buttonId, int x, int y, int widthIn, int heightIn, String onText, String offText)
    {
        super(buttonId, x, y, widthIn, heightIn, offText);

        this.onText = onText;
        this.offText = offText;
    }

    public void setValue(boolean value)
    {
        this.value = value;
        this.displayString = value ? this.onText : this.offText;
    }

    public boolean getValue()
    {
        return this.value;
    }

    public void toggle()
    {
        this.setValue(!this.value);
    }
}
