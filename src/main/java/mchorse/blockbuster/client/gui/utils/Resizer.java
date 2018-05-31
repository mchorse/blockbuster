package mchorse.blockbuster.client.gui.utils;

import mchorse.blockbuster.client.gui.framework.GuiElement;

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

    public Resizer setRelative(Resizer relative)
    {
        this.relative = relative;

        return this;
    }

    public Resizer setParent(Area parent)
    {
        this.parent = parent;

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

        if (this.parent != null && this.x.unit == UnitMeasurement.PERCENTAGE)
        {
            value = (int) (this.parent.w * this.x.value);
        }

        if (this.relative != null)
        {
            value += this.relative.getX();
        }

        return value + this.x.padding;
    }

    public int getY()
    {
        int value = (int) this.y.value;

        if (this.parent != null && this.y.unit == UnitMeasurement.PERCENTAGE)
        {
            value = (int) (this.parent.h * this.y.value);
        }

        if (this.relative != null)
        {
            value += this.relative.getY();
        }

        return value + this.y.padding;
    }

    public int getW()
    {
        int value = (int) this.w.value;

        if (this.parent != null && this.w.unit == UnitMeasurement.PERCENTAGE)
        {
            value = (int) (this.parent.w * this.w.value) + this.w.padding;
        }

        return value;
    }

    public int getH()
    {
        int value = (int) this.h.value;

        if (this.parent != null && this.h.unit == UnitMeasurement.PERCENTAGE)
        {
            value = (int) (this.parent.h * this.h.value) + this.h.padding;
        }

        return value;
    }

    /**
     * Unit class
     */
    public static class Unit
    {
        public float value;
        public int padding;
        public boolean enabled = true;
        public UnitMeasurement unit = UnitMeasurement.PIXELS;

        public void set(float value, UnitMeasurement unit)
        {
            this.set(value, unit, 0);
        }

        public void set(float value, UnitMeasurement unit, int padding)
        {
            this.value = value;
            this.unit = unit;
            this.padding = padding;
        }
    }

    /**
     * Unit measurement for sizer class. This determines logic for 
     * calculating units.
     */
    public static enum UnitMeasurement
    {
        PIXELS, PERCENTAGE;
    }
}