package mchorse.blockbuster.client.gui.dashboard.panels.model_editor;

import java.util.function.Consumer;

import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import net.minecraft.client.Minecraft;

public class GuiTwoElement extends GuiElement
{
    public GuiTrackpadElement a;
    public GuiTrackpadElement b;
    public Float[] array;

    public GuiTwoElement(Minecraft mc, Consumer<Float[]> a)
    {
        super(mc);

        this.array = new Float[] {0F, 0F};
        this.a = new GuiTrackpadElement(mc, "", (value) ->
        {
            this.array[0] = value;
            a.accept(this.array);
        });
        this.b = new GuiTrackpadElement(mc, "", (value) ->
        {
            this.array[1] = value;
            a.accept(this.array);
        });

        this.a.resizer().parent(this.area).set(0, 0, 0.5F, 1, Measure.RELATIVE);
        this.a.resizer().w.padding = -2;
        this.b.resizer().parent(this.area).set(0.5F, 0, 0.5F, 1, Measure.RELATIVE);
        this.b.resizer().w.padding = -2;
        this.b.resizer().x.padding = 2;

        this.createChildren();
        this.children.add(this.a);
        this.children.add(this.b);
    }

    public void setLimit(int min, int max)
    {
        this.a.trackpad.min = this.b.trackpad.min = min;
        this.a.trackpad.max = this.b.trackpad.max = max;
    }

    public void setLimit(int min, int max, boolean integer)
    {
        this.setLimit(min, max);
        this.a.trackpad.integer = this.b.trackpad.integer = integer;
    }

    public void setValues(float a, float b)
    {
        this.a.trackpad.setValue(a);
        this.b.trackpad.setValue(b);

        this.array[0] = a;
        this.array[1] = b;
    }
}