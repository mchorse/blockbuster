package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils;

import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.resizers.Flex;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public class GuiThreeElement extends GuiTwoElement
{
    public GuiTrackpadElement c;

    public GuiThreeElement(Minecraft mc, Consumer<Double[]> callback)
    {
        super(mc, callback);

        this.array = new Double[] {0D, 0D, 0D};
        this.c = new GuiTrackpadElement(mc, (value) ->
        {
            this.array[2] = value;

            if (callback != null)
            {
                callback.accept(this.array);
            }
        });
        this.add(this.c);
    }

    @Override
    public void setLimit(int min, int max)
    {
        super.setLimit(min, max);
        this.c.min = min;
        this.c.max = max;
    }

    @Override
    public void setLimit(int min, int max, boolean integer)
    {
        super.setLimit(min, max, integer);
        this.c.limit(min, max, integer);
    }

    public void setValues(double a, double b, double c)
    {
        this.setValues(a, b);
        this.c.setValue(c);

        this.array[2] = c;
    }
}