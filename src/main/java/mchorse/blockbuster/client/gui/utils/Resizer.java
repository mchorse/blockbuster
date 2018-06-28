package mchorse.blockbuster.client.gui.utils;

import mchorse.blockbuster.client.gui.framework.elements.GuiElement;

/**
 * Resizer class
 * 
 * This class is used to define resizing behavior for a 
 * {@link GuiElement}. 
 */
public class Resizer
{
    public Unit x = new Unit();
    public Unit y = new Unit();
    public Unit w = new Unit();
    public Unit h = new Unit();
    public float anchorX;
    public float anchorY;

    public Resizer relative;
    public Area parent;

    public Resizer set(float x, float y, float w, float h)
    {
        this.x.value = x;
        this.y.value = y;
        this.w.value = w;
        this.h.value = h;

        return this;
    }

    public Resizer set(float x, float y, float w, float h, Measure measure)
    {
        this.x.unit = measure;
        this.y.unit = measure;
        this.w.unit = measure;
        this.h.unit = measure;

        return this.set(x, y, w, h);
    }

    public Resizer x(int value)
    {
        this.x.set(value, Measure.PIXELS, 0);

        return this;
    }

    public Resizer x(float value, int padding)
    {
        this.x.set(value, Measure.RELATIVE, padding);

        return this;
    }

    public Resizer y(int value)
    {
        this.y.set(value, Measure.PIXELS, 0);

        return this;
    }

    public Resizer y(float value, int padding)
    {
        this.y.set(value, Measure.RELATIVE, padding);

        return this;
    }

    public Resizer w(int value)
    {
        this.w.set(value, Measure.PIXELS, 0);

        return this;
    }

    public Resizer w(float value, int padding)
    {
        this.w.set(value, Measure.RELATIVE, padding);

        return this;
    }

    public Resizer h(int value)
    {
        this.h.set(value, Measure.PIXELS, 0);

        return this;
    }

    public Resizer h(float value, int padding)
    {
        this.h.set(value, Measure.RELATIVE, padding);

        return this;
    }

    public Resizer relative(Resizer relative)
    {
        this.relative = relative;
        this.parent = null;

        return this;
    }

    public Resizer parent(Area parent)
    {
        this.parent = parent;
        this.relative = null;

        return this;
    }

    public void apply(Area area)
    {
        if (this.w.enabled) area.w = this.getW();
        if (this.h.enabled) area.h = this.getH();
        if (this.x.enabled) area.x = this.getX();
        if (this.y.enabled) area.y = this.getY();
    }

    public int getX()
    {
        int value = (int) this.x.value;

        if (this.relative != null)
        {
            value += this.relative.getX();

            if (this.x.unit == Measure.RELATIVE)
            {
                value = this.relative.getX() + (int) (this.relative.getW() * this.x.value);
            }
        }
        else if (this.parent != null)
        {
            value += this.parent.x;

            if (this.x.unit == Measure.RELATIVE)
            {
                value = this.parent.x + (int) (this.parent.w * this.x.value);
            }
        }

        return value + this.x.padding;
    }

    public int getY()
    {
        int value = (int) this.y.value;

        if (this.relative != null)
        {
            value += this.relative.getY();

            if (this.y.unit == Measure.RELATIVE)
            {
                value = this.relative.getY() + (int) (this.relative.getH() * this.y.value);
            }
        }
        else if (this.parent != null)
        {
            value += this.parent.y;

            if (this.y.unit == Measure.RELATIVE)
            {
                value = this.parent.y + (int) (this.parent.h * this.y.value);
            }
        }

        return value + this.y.padding;
    }

    public int getW()
    {
        int value = (int) this.w.value;

        if (this.parent != null && this.w.unit == Measure.RELATIVE)
        {
            value = (int) (this.parent.w * this.w.value);
        }

        return value + this.w.padding;
    }

    public int getH()
    {
        int value = (int) this.h.value;

        if (this.parent != null && this.h.unit == Measure.RELATIVE)
        {
            value = (int) (this.parent.h * this.h.value);
        }

        return value + this.h.padding;
    }

    /**
     * Unit class
     */
    public static class Unit
    {
        public float value;
        public int padding;
        public boolean enabled = true;
        public Measure unit = Measure.PIXELS;

        public void set(float value, Measure unit)
        {
            this.set(value, unit, 0);
        }

        public void set(float value, Measure unit, int padding)
        {
            this.value = value;
            this.unit = unit;
            this.padding = padding;
        }

        public void disable()
        {
            this.enabled = false;
        }
    }

    /**
     * Unit measurement for sizer class. This determines logic for 
     * calculating units.
     * 
     * {@link Measure#PIXELS} are absolute. Meanwhile 
     * {@link Measure#RELATIVE} are percentage (or rather a scalar 
     * between 0 and 1 equaling to 0% to 100%). 
     */
    public static enum Measure
    {
        PIXELS, RELATIVE;
    }
}