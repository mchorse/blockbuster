package mchorse.blockbuster.client.gui.widgets.buttons;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCirculate extends GuiButton
{
    protected List<String> labels = new ArrayList<String>();
    protected int value = 0;

    public GuiCirculate(int buttonId, int x, int y, int widthIn, int heightIn)
    {
        super(buttonId, x, y, widthIn, heightIn, "");
    }

    public void addLabel(String label)
    {
        this.labels.add(label);
    }

    public int getValue()
    {
        return this.value;
    }

    public String getLabel()
    {
        return this.labels.get(this.value);
    }

    public void setValue(int value)
    {
        this.value = value;

        if (this.value > this.labels.size() - 1)
        {
            this.value = 0;
        }

        if (this.value < 0)
        {
            this.value = this.labels.size() - 1;
        }

        this.displayString = this.labels.get(this.value);
    }

    public void toggle()
    {
        this.setValue(this.value + 1);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        boolean result = super.mousePressed(mc, mouseX, mouseY);

        if (result)
        {
            this.toggle();
        }

        return result;
    }
}
