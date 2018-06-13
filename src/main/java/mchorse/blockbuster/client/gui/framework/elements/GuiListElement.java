package mchorse.blockbuster.client.gui.framework.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.client.gui.utils.ScrollArea;
import mchorse.metamorph.client.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

public class GuiListElement extends GuiElement
{
    public List<String> list = new ArrayList<String>();
    public ScrollArea scroll = new ScrollArea(16);
    public Consumer<String> callback;
    public int current = -1;

    public GuiListElement(Minecraft mc, Consumer<String> callback)
    {
        super(mc);

        this.callback = callback;
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

        GuiUtils.scissor(this.scroll.x, this.scroll.y, this.scroll.w, this.scroll.h, screen.width, screen.height);

        for (String str : this.list)
        {
            int x = this.area.x;
            int y = this.area.y + i * this.scroll.scrollItemSize - this.scroll.scroll;
            boolean hover = mouseX >= x && mouseY >= y && mouseX < x + this.scroll.w && mouseY < y + this.scroll.scrollItemSize;

            if (this.current == i)
            {
                Gui.drawRect(x, y, x + this.scroll.w, y + this.scroll.scrollItemSize, 0x880088ff);
            }

            this.font.drawStringWithShadow(str, x + 4, y + 4, hover ? 16777120 : 0xffffff);

            i++;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        this.scroll.drawScrollbar();
    }
}