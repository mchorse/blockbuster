package mchorse.blockbuster.client.gui.dashboard.panels.model_editor;

import java.util.function.Consumer;

import mchorse.blockbuster.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import net.minecraft.client.Minecraft;

public class GuiThreeElement extends GuiTwoElement
{
    public GuiTrackpadElement c;

    public GuiThreeElement(Minecraft mc, Consumer<Float[]> a)
    {
        super(mc, a);

        this.array = new Float[] {0F, 0F, 0F};
        this.c = new GuiTrackpadElement(mc, "", (value) ->
        {
            this.array[2] = value;
            a.accept(this.array);
        });
        this.children.add(this.c);

        this.c.resizer().parent(this.area).set(0.333F, 0, 0.333F, 1, Measure.RELATIVE);

        this.a.resizer().w.value = this.b.resizer().w.value = 0.333F;
        this.b.resizer().x.value = 0.666F;
    }

    public void setLimit(int min, int max)
    {
        super.setLimit(min, max);
        this.c.trackpad.min = min;
        this.c.trackpad.max = max;
    }

    public void setValues(float a, float b, float c)
    {
        this.setValues(a, b);
        this.c.trackpad.setValue(c);
    }
}