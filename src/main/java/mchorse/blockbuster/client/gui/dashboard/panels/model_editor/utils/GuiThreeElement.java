package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils;

import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.resizers.Flex;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public class GuiThreeElement extends GuiTwoElement
{
    public GuiTrackpadElement c;

    public GuiThreeElement(Minecraft mc, Consumer<Float[]> a)
    {
        super(mc, a);

        this.array = new Float[] {0F, 0F, 0F};
        this.c = new GuiTrackpadElement(mc, (value) ->
        {
            this.array[2] = value.floatValue();
            a.accept(this.array);
        });
        this.add(this.c);

        this.c.flex().relative(this.area).set(0.667F, 0, 0.333F, 1, Flex.Measure.RELATIVE);

        this.a.flex().w.value = this.b.flex().w.value = 0.333F;
        this.b.flex().x.set(0.333F, Flex.Measure.RELATIVE, 0);
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
        this.c.min = min;
        this.c.max = max;
        this.c.integer = integer;
    }

    public void setValues(float a, float b, float c)
    {
        this.setValues(a, b);
        this.c.setValue(c);

        this.array[2] = c;
    }
}