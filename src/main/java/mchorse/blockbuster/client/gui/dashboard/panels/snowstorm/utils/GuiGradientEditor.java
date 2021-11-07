package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.utils;

import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections.GuiSnowstormSection;
import mchorse.blockbuster.client.particles.components.appearance.Tint;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiContextMenu;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.math.Constant;
import mchorse.mclib.math.molang.expressions.MolangValue;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class GuiGradientEditor extends GuiElement
{
    private GuiSnowstormSection section;
    private GuiColorElement color;

    private Tint.Gradient gradient;

    private Tint.Gradient.ColorStop current;
    private int dragging = -1;
    private int lastX;

    private Area a = new Area();
    private Area b = new Area();
    private Color c = new Color();

    public GuiGradientEditor(Minecraft mc, GuiSnowstormSection section, GuiColorElement color)
    {
        super(mc);

        this.section = section;
        this.color = color;

        this.flex().h(20);
    }

    private Color fillColor(Tint.Solid solid)
    {
        this.c.r = (float) solid.r.get();
        this.c.g = (float) solid.g.get();
        this.c.b = (float) solid.b.get();
        this.c.a = (float) solid.a.get();

        return this.c;
    }

    private Area fillBound(Tint.Gradient.ColorStop stop)
    {
        int x = this.a.x(stop.stop / this.gradient.range);

        this.b.set(x - 3, this.a.ey() - 7, 6, 10);

        return this.b;
    }

    private void fillStop(Tint.Gradient.ColorStop stop)
    {
        this.current = stop;
        this.color.picker.setColor(this.fillColor(stop.color).getRGBAColor());
    }

    public void setColor(int color)
    {
        this.c.set(color, true);

        ((MolangValue) this.current.color.r).value.set(this.c.r);
        ((MolangValue) this.current.color.g).value.set(this.c.g);
        ((MolangValue) this.current.color.b).value.set(this.c.b);
        ((MolangValue) this.current.color.a).value.set(this.c.a);
    }

    public void setGradient(Tint.Gradient gradient)
    {
        this.gradient = gradient;

        if (this.gradient.stops.isEmpty())
        {
            this.gradient.stops.add(new Tint.Gradient.ColorStop(0, new Tint.Solid()));
        }

        this.fillStop(this.gradient.stops.get(0));
        this.color.picker.setColor(this.fillColor(this.current.color).getRGBAColor());
    }

    @Override
    public GuiContextMenu createContextMenu(GuiContext context)
    {
        GuiSimpleContextMenu menu = new GuiSimpleContextMenu(context.mc);

        menu.action(Icons.ADD, IKey.lang("blockbuster.gui.snowstorm.lighting.context.add_stop"), () -> this.addColorStop(context.mouseX));

        if (this.gradient.stops.size() > 1)
        {
            menu.action(Icons.REMOVE, IKey.lang("blockbuster.gui.snowstorm.lighting.context.remove_stop"), this::removeColorStop);
        }

        return menu;
    }

    private void addColorStop(int mouseX)
    {
        float x = (mouseX - this.area.x) / (float) this.area.w * this.gradient.range;

        Tint.Solid color = new Tint.Solid();
        Tint.Gradient.ColorStop stop = new Tint.Gradient.ColorStop(x, color);

        color.r = new MolangValue(null, new Constant(1F));
        color.g = new MolangValue(null, new Constant(1F));
        color.b = new MolangValue(null, new Constant(1F));
        color.a = new MolangValue(null, new Constant(1F));

        this.gradient.stops.add(stop);
        this.gradient.sort();

        this.fillStop(stop);
    }

    private void removeColorStop()
    {
        int index = this.gradient.stops.indexOf(this.current);

        this.gradient.stops.remove(index);

        index = MathUtils.clamp(index, 0, this.gradient.stops.size() - 1);

        this.fillStop(this.gradient.stops.get(index));
    }

    @Override
    public void resize()
    {
        super.resize();

        this.a.copy(this.area);
        this.a.offset(-1);
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        if (this.area.isInside(context))
        {
            for (Tint.Gradient.ColorStop stop : this.gradient.stops)
            {
                Area area = this.fillBound(stop);

                if (area.isInside(context))
                {
                    this.dragging = 0;
                    this.lastX = context.mouseX;
                    this.fillStop(stop);

                    return true;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        super.mouseReleased(context);

        if (this.dragging != -1)
        {
            this.section.dirty();
        }

        this.dragging = -1;
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.dragging == 0 && Math.abs(context.mouseX - this.lastX) > 3)
        {
            this.dragging = 1;
        }
        else if (this.dragging == 1)
        {
            float x = (context.mouseX - this.area.x) / (float) this.area.w * this.gradient.range;

            this.current.stop = MathUtils.clamp(x, 0, this.gradient.range);
            this.gradient.sort();
        }

        this.area.draw(0xff000000);

        int size = this.gradient.stops.size();

        GlStateManager.color(1, 1, 1, 1);
        Icons.CHECKBOARD.renderArea(this.a.x, this.a.y, this.a.w, this.a.h);

        Tint.Gradient.ColorStop first = this.gradient.stops.get(0);

        if (first.stop > 0)
        {
            int x1 = this.a.x(first.stop / this.gradient.range);
            int rgba1 = this.fillColor(first.color).getRGBAColor();

            Gui.drawRect(this.a.x, this.a.y, x1, this.a.ey(), rgba1);
        }

        for (int i = 0; i < size; i++)
        {
            Tint.Gradient.ColorStop stop = this.gradient.stops.get(i);
            Tint.Gradient.ColorStop next = i + 1 < size ? this.gradient.stops.get(i + 1) : stop;

            int x1 = this.a.x(stop.stop / this.gradient.range);
            int x2 = this.a.x((next == stop ? this.gradient.range : next.stop) / this.gradient.range);

            int rgba1 = this.fillColor(stop.color).getRGBAColor();
            int rgba2 = this.fillColor(next.color).getRGBAColor();

            GuiDraw.drawHorizontalGradientRect(x1, this.a.y, x2, this.a.ey(), rgba1, rgba2);
        }

        for (int i = 0; i < size; i++)
        {
            Tint.Gradient.ColorStop stop = this.gradient.stops.get(i);
            Area area = this.fillBound(stop);

            int x = this.a.x(stop.stop / this.gradient.range);

            Gui.drawRect(area.x, area.y, area.ex(), area.ey(), this.current == stop ? 0xffffffff : 0xff000000);
            Gui.drawRect(area.x + 1, area.y + 1, area.ex() - 1, area.ey() - 1, this.fillColor(stop.color).getRGBAColor());
        }

        super.draw(context);
    }
}