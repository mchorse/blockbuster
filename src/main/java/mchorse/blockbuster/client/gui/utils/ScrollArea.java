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
     * Speed of how fast shit's scrolling  
     */
    public int scrollSpeed = 5;

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
        int size = this.direction.getSide(this);

        if (this.scrollSize <= size)
        {
            this.scroll = 0;
        }
        else
        {
            this.scroll = MathHelper.clamp(this.scroll, 0, this.scrollSize - size);
        }
    }

    /**
     * Get index of the cursor based on the {@link #scrollItemSize}.  
     */
    public int getIndex(int x, int y)
    {
        int axis = this.direction.getScroll(this, x, y);
        int index = axis / this.scrollItemSize;

        if (axis < 0 || axis > this.scrollSize)
        {
            return -1;
        }

        return index > this.scrollSize / this.scrollItemSize ? -1 : index;
    }

    /**
     * Calculates scroll bar's height 
     */
    public int getScrollBar(int size)
    {
        int maxSize = this.direction.getSide(this);

        if (this.scrollSize < size)
        {
            return 0;
        }

        return (int) ((1.0F - ((this.scrollSize - maxSize) / (float) this.scrollSize)) * size);
    }

    /* GUI code for easier manipulations */

    /**
     * This method should be invoked to register dragging 
     */
    public boolean mouseClicked(int x, int y)
    {
        boolean isInside = this.isInside(x, y) && this.scrollSize > this.h && (this.direction == ScrollDirection.VERTICAL ? x >= this.getX(1) - 4 : y >= this.getY(1) - 4);

        if (isInside)
        {
            this.dragging = true;
        }

        return isInside;
    }

    /**
     * This method should be invoked when mouse wheel is scrolling 
     */
    public boolean mouseScroll(int x, int y, int scroll)
    {
        boolean isInside = this.isInside(x, y);

        if (isInside)
        {
            this.scrollBy((int) Math.copySign(this.scrollSpeed, scroll));
        }

        return isInside;
    }

    /**
     * When mouse button gets released
     */
    public void mouseReleased(int x, int y)
    {
        this.dragging = false;
    }

    /**
     * This should be invoked in a drawing or and update method. It's 
     * responsible for scrolling through this view when dragging. 
     */
    public void drag(int x, int y)
    {
        if (this.dragging)
        {
            float progress = this.direction.getProgress(this, x, y);

            this.scrollTo((int) (progress * (this.scrollSize - this.direction.getSide(this) + 4)));
        }
    }

    /**
     * This method is responsible for drawing a scroll bar 
     */
    public void drawScrollbar()
    {
        int side = this.direction.getSide(this);

        if (this.scrollSize <= side)
        {
            return;
        }

        int h = this.getScrollBar(side / 2);
        int x = this.getX(1) - 4;
        /* Sometimes I don't understand how I come up with such clever
         * formulas, but it's all ratios, y'all */
        int y = this.y + (int) ((this.scroll / (float) (this.scrollSize - this.h)) * (this.h - h));

        if (this.direction == ScrollDirection.HORIZONTAL)
        {
            y = this.getY(1) - 4;
            x = this.x + (int) ((this.scroll / (float) (this.scrollSize - this.w)) * (this.w - h));

            Gui.drawRect(x, y, x + h, y + 4, -6250336);
        }
        else
        {
            Gui.drawRect(x, y, x + 4, y + h, -6250336);
        }
    }

    /**
     * Scroll direction 
     */
    public static enum ScrollDirection
    {
        VERTICAL()
        {
            @Override
            public int getSide(ScrollArea area)
            {
                return area.h;
            }

            @Override
            public int getScroll(ScrollArea area, int x, int y)
            {
                return y - area.y + area.scroll;
            }

            @Override
            public float getProgress(ScrollArea area, int x, int y)
            {
                return (y - area.y) / (float) area.h;
            }
        },
        HORIZONTAL()
        {
            @Override
            public int getSide(ScrollArea area)
            {
                return area.w;
            }

            @Override
            public int getScroll(ScrollArea area, int x, int y)
            {
                return x - area.x + area.scroll;
            }

            @Override
            public float getProgress(ScrollArea area, int x, int y)
            {
                return (x - area.x) / (float) area.w;
            }
        };

        /**
         * Get dominant side for this scrolling direction 
         */
        public abstract int getSide(ScrollArea area);

        /**
         * Get scrolled amount for given mouse position 
         */
        public abstract int getScroll(ScrollArea area, int x, int y);

        /**
         * Get progress scalar between 0 and 1 which identifies how much 
         * it is near the maximum side 
         */
        public abstract float getProgress(ScrollArea area, int x, int y);
    }
}