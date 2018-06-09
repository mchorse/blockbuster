package mchorse.blockbuster.client.gui.dashboard.panels;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.utils.ScrollArea;
import mchorse.metamorph.client.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.BlockPos;

/**
 * GUI block list
 * 
 * This GUI module is responsible for rendering and selecting 
 */
public abstract class GuiBlockList<T> extends GuiElement
{
    /**
     * List of elements 
     */
    public List<T> elements = new ArrayList<T>();

    /**
     * Currently selected element 
     */
    public T current;

    /**
     * Callback which gets invoked when user selects a block
     */
    public Consumer<T> callback;

    /**
     * Title of this panel 
     */
    public String title;

    /**
     * Scrolling section where block entries are getting rendered 
     */
    public ScrollArea scroll;

    public GuiBlockList(Minecraft mc, String title, Consumer<T> callback)
    {
        super(mc);

        this.title = title;
        this.scroll = new ScrollArea(20);
        this.callback = callback;
    }

    public abstract boolean addBlock(BlockPos pos);

    public abstract void render(int x, int y, T item, boolean hovered);

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.scroll.set(this.area.x, this.area.y + 30, this.area.w, this.area.h - 30);
        this.scroll.clamp();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.scroll.mouseClicked(mouseX, mouseY))
        {
            return true;
        }

        if (this.scroll.isInside(mouseX, mouseY))
        {
            int index = this.scroll.getIndex(mouseX, mouseY);
            int size = this.elements.size();

            if (this.callback != null && index >= 0 && index < size && size != 0)
            {
                this.current = this.elements.get(index);
                this.callback.accept(this.current);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int scroll)
    {
        return super.mouseScrolled(mouseX, mouseY, scroll) || this.scroll.mouseScroll(mouseX, mouseY, scroll);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);

        this.scroll.mouseReleased(mouseX, mouseY);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.mc.renderEngine.bindTexture(GuiDashboard.ICONS);
        net.minecraftforge.fml.client.config.GuiUtils.drawContinuousTexturedBox(this.area.x, this.area.y, 0, 64, this.area.w, this.area.h, 32, 32, 0, 0);

        Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.y + 30, 0x44000000);
        this.font.drawStringWithShadow(this.title, this.area.x + 10, this.area.y + 11, 0xcccccc);
        this.scroll.drag(mouseX, mouseY);

        GuiScreen screen = this.mc.currentScreen;
        GuiUtils.scissor(this.scroll.x, this.scroll.y, this.scroll.w, this.scroll.h, screen.width, screen.height);

        int i = 0;
        int h = this.scroll.scrollItemSize;

        for (T element : this.elements)
        {
            int x = this.scroll.x;
            int y = this.scroll.y + i * 20 - this.scroll.scroll;

            boolean hovered = mouseX >= x && mouseX <= this.area.getX(1) - 10;
            hovered = hovered && mouseY >= y && mouseY < y + h;

            this.render(x, y, element, hovered);
            Gui.drawRect(x, y + h - 1, x + this.area.w, y + h, 0xaa181818);

            i++;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        this.scroll.drawScrollbar();

        super.draw(mouseX, mouseY, partialTicks);
    }
}