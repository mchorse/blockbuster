package mchorse.blockbuster.client.gui.utils;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.MathHelper;

/**
 * Scrollable area
 * 
 * This class is responsible for storing information for scrollable one 
 * directional objects. 
 */
public class ScrollArea extends Area
{
    /**
     * Size of an element/item in the scroll area
     */
    public int scrollItemSize;

    /**
     * Size of the scrolling area 
     */
    public int scrollSize;

    /**
     * Scroll position 
     */
    public int scroll;

    /**
     * Whether this scroll area gets dragged 
     */
    public boolean dragging;

    /**
     * Scroll direction, used primarily in the {@link #clamp()} method 
     */
    public ScrollDirection direction = ScrollDirection.VERTICAL;

    public ScrollArea(int itemSize)
    {
        this.scrollItemSize = itemSize;
    }

    public void setSize(int items)
    {
        this.scrollSize = items * this.scrollItemSize;
    }

    /**
     * Scroll by relative amount 
     */
    public void scrollBy(int x)
    {
        this.scroll += x;
        this.clamp();
    }

    /**
     * Scroll to the position in the scroll area 
     */
    public void scrollTo(int x)
    {
        this.scroll = x;
        this.clamp();
    }

    /**
     * Clamp scroll to the bounds of the scroll size; 
     */
    public void clamp()
    {
        int size = this.direction == ScrollDirection.VERTICAL ? this.h : this.w;

        if (this.scrollSize <= size)
        {
            this.scroll = 0;
        }
        else
        {
            this.scroll = MathHelper.clamp_int(this.scroll, 0, this.scrollSize - size);
        }
    }

    /**
     * Get index of the cursor based on the {@link #scrollItemSize}.  
     */
    public int getIndex(int x, int y)
    {
        if (!this.isInside(x, y))
        {
            return -1;
        }

        int axis = 0;

        if (this.direction == ScrollDirection.VERTICAL)
        {
            y -= this.y;
            y += this.scroll;

            axis = y;
        }
        else
        {
            x -= this.x;
            x += this.scroll;

            axis = x;
        }

        int index = axis / this.scrollItemSize;

        return index > this.scrollSize / this.scrollItemSize ? -1 : index;
    }

    /**
     * Calculates scroll bar's height 
     */
    public int getScrollBar(int size)
    {
        int maxSize = this.direction == ScrollDirection.VERTICAL ? this.h : this.w;

        if (this.scrollSize < size)
        {
            return 0;
        }

        return (int) ((1.0F - ((this.scrollSize - maxSize) / (float) this.scrollSize)) * size);
    }

    /* GUI code */

    public boolean mouseClicked(int x, int y)
    {
        boolean isInside = this.isInside(x, y) && this.scrollSize > this.h && x >= this.getX(1) - 4;

        if (isInside)
        {
            this.dragging = true;
        }

        return isInside;
    }

    public boolean mouseScroll(int x, int y, int scroll)
    {
        boolean isInside = this.isInside(x, y);

        if (isInside)
        {
            this.scrollBy(scroll);
        }

        return isInside;
    }

    public void mouseReleased(int x, int y)
    {
        this.dragging = false;
    }

    public void drag(int x, int y)
    {
        if (this.dragging)
        {
            float progress = (float) (y - this.y) / (float) this.h;

            this.scrollTo((int) (progress * (this.scrollSize - this.h + 4)));
        }
    }

    public void drawScrollbar()
    {
        if (this.scrollSize <= this.h)
        {
            return;
        }

        int h = this.getScrollBar(this.h / 2);
        int x = this.getX(1) - 4;
        int y = this.y + (int) ((this.scroll / (float) (this.scrollSize - this.h)) * (this.h - h));

        Gui.drawRect(x, y, x + 4, y + h, -6250336);
    }

    /**
     * Scroll direction 
     */
    public static enum ScrollDirection
    {
        VERTICAL, HORIZONTAL;
    }
}