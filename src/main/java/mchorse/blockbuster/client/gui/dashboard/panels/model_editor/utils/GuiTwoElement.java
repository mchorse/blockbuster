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
    public Float[] array;

    public GuiTwoElement(Minecraft mc, Consumer<Float[]> a)
    {
        super(mc);

        this.array = new Float[] {0F, 0F};
        this.a = new GuiTrackpadElement(mc, (value) ->
        {
            this.array[0] = value;
            a.accept(this.array);
        });
        this.b = new GuiTrackpadElement(mc, (value) ->
        {
            this.array[1] = value;
            a.accept(this.array);
        });

        this.a.flex().relative(this.area).set(0, 0, 0.5F, 1, Flex.Measure.RELATIVE);
        this.a.flex().w.offset = -2;
        this.b.flex().relative(this.area).set(0.5F, 0, 0.5F, 1, Flex.Measure.RELATIVE);
        this.b.flex().w.offset = -2;
        this.b.flex().x.offset = 2;

        this.add(this.a);
        this.add(this.b);
    }

    public void setLimit(int min, int max)
    {
        this.a.min = this.b.min = min;
        this.a.max = this.b.max = max;
    }

    public void setLimit(int min, int max, boolean integer)
    {
        this.setLimit(min, max);
        this.a.integer = this.b.integer = integer;
    }

    public void setValues(float a, float b)
    {
        this.a.setValue(a);
        this.b.setValue(b);

        this.array[0] = a;
        this.array[1] = b;
    }
}