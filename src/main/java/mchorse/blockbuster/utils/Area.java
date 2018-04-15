package mchorse.blockbuster.utils;

/**
 * Utility class for boxes
 *
 * Used in GUI for rendering and locating cursor inside of the box purposes.
 */
public class Area
{
    /**
     * X position coordinate of the box
     */
    public int x;

    /**
     * Y position coordinate of the box
     */
    public int y;

    /**
     * Width of the box
     */
    public int w;

    /**
     * Height of the box
     */
    public int h;

    /**
     * Check whether given position is inside of the rect
     */
    public boolean isInside(int x, int y)
    {
        return x >= this.x && x < this.x + this.w && y >= this.y && y < this.y + this.h;
    }

    /**
     * Set all values
     */
    public void set(int x, int y, int w, int h)
    {
        this.setPos(x, y);
        this.setSize(w, h);
    }

    /**
     * Set the position
     */
    public void setPos(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Set the size
     */
    public void setSize(int w, int h)
    {
        this.w = w;
        this.h = h;
    }
}
