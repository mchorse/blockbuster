package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.resizers.Flex;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public class GuiTwoElement extends GuiElement
{
    public GuiTrackpadElement a;
    public GuiTrackpadElement b;
    public Double[] array;

    public GuiTwoElement(Minecraft mc, Consumer<Double[]> callback)
    {
        super(mc);

        this.array = new Double[] {0D, 0D};
        this.a = new GuiTrackpadElement(mc, (value) ->
        {
            this.array[0] = value;

            if (callback != null)
            {
                callback.accept(this.array);
            }
        });
        this.b = new GuiTrackpadElement(mc, (value) ->
        {
            this.array[1] = value;

            if (callback != null)
            {
                callback.accept(this.array);
            }
        });

        this.flex().h(20).row(5);
        this.add(this.a, this.b);
    }

    public void setLimit(int min, int max)
    {
        this.a.limit(min, max);
        this.b.limit(min, max);
    }

    public void setLimit(int min, int max, boolean integer)
    {
        this.a.limit(min, max, integer);
        this.b.limit(min, max, integer);
    }

    public void setValues(double a, double b)
    {
        this.a.setValue(a);
        this.b.setValue(b);

        this.array[0] = a;
        this.array[1] = b;
    }
}