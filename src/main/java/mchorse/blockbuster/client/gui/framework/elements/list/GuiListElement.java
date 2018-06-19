package mchorse.blockbuster.client.gui.framework.elements.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.utils.ScrollArea;
import mchorse.metamorph.client.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

/**
 * Abstract GUI list element
 * 
 * This element allows managing scrolling lists much easier
 */
public abstract class GuiListElement<T> extends GuiElement
{
    /**
     * List of elements 
     */
    protected List<T> list = new ArrayList<T>();

    /**
     * Scrolling area
     */
    public ScrollArea scroll = new ScrollArea(20);

    /**
     * Callback which gets invoked when user selects an element
     */
    public Consumer<T> callback;

    /**
     * Current index of selected element 
     */
    public int current = -1;

    public GuiListElement(Minecraft mc, Consumer<T> callback)
    {
        super(mc);

        this.callback = callback;
    }

    public void clear()
    {
        this.current = -1;
        this.list.clear();
        this.update();
    }

    public void add(T element)
    {
        this.list.add(element);
        this.update();
    }

    public void add(Collection<T> elements)
    {
        this.list.addAll(elements);
        this.update();
    }

    public void replace(T element)
    {
        int size = this.list.size();

        if (this.current >= 0 && this.current < size)
        {
            this.list.set(this.current, element);
        }
    }

    public void setCurrent(T element)
    {
        this.current = this.list.indexOf(element);
    }

    public void remove(T element)
    {
        this.list.remove(element);
    }

    public void update()
    {
        this.scroll.setSize(this.list.size());
        this.scroll.clamp();
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.scroll.copy(this.area);
        this.scroll.clamp();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.scroll.mouseClicked(mouseX, mouseY))
        {
            return true;
        }

        if (this.scroll.isInside(mouseX, mouseY))
        {
            int index = this.scroll.getIndex(mouseX, mouseY);
            int size = this.list.size();

            if (index >= 0 && index < size)
            {
                this.current = index;

                if (this.callback != null)
                {
                    this.callback.accept(this.list.get(index));

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int scroll)
    {
        return this.scroll.mouseScroll(mouseX, mouseY, scroll);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.scroll.mouseReleased(mouseX, mouseY);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.scroll.drag(mouseX, mouseY);

        GuiScreen screen = this.mc.currentScreen;
        int i = 0;
        int h = this.scroll.scrollItemSize;

        GuiUtils.scissor(this.scroll.x, this.scroll.y, this.scroll.w, this.scroll.h, screen.width, screen.height);

        for (T element : this.list)
        {
            int x = this.scroll.x;
            int y = this.scroll.y + i * h - this.scroll.scroll;

            if (y + h < this.scroll.y)
            {
                i++;
                continue;
            }

            if (y >= this.scroll.getY(1))
            {
                break;
            }

            boolean hover = mouseX >= x && mouseY >= y && mouseX < x + this.scroll.w && mouseY < y + this.scroll.scrollItemSize;

            this.drawElement(element, i, x, y, hover);

            i++;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        this.scroll.drawScrollbar();

        super.draw(mouseX, mouseY, partialTicks);
    }

    /**
     * Draw individual element 
     */
    public abstract void drawElement(T element, int i, int x, int y, boolean hover);
}