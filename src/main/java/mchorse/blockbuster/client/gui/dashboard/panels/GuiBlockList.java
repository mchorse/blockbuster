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

public abstract class GuiBlockList<T> extends GuiElement
{
    public List<T> elements = new ArrayList<T>();
    public T current;
    public Consumer<T> callback;
    public String title;
    public ScrollArea scroll;

    public GuiBlockList(Minecraft mc, String title, Consumer<T> callback)
    {
        super(mc);

        this.title = title;
        this.scroll = new ScrollArea(20);
        this.callback = callback;
    }

    public abstract void addBlock(BlockPos pos);

    public abstract void render(int x, int y, T item, boolean hovered);

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.scroll.set(this.area.x + 10, this.area.y + 30, this.area.w - 20, this.area.h - 40);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.scroll.isInside(mouseX, mouseY))
        {
            if (mouseX >= this.scroll.getX(1) - 4)
            {
                this.scroll.dragging = true;
            }

            int index = this.scroll.getIndex(mouseX, mouseY);
            int size = this.elements.size();

            if (this.callback != null && index >= 0 && index < size && size != 0)
            {
                this.callback.accept(this.elements.get(index));
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);

        this.scroll.dragging = false;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.mc.renderEngine.bindTexture(GuiDashboard.ICONS);
        net.minecraftforge.fml.client.config.GuiUtils.drawContinuousTexturedBox(this.area.x, this.area.y, 0, 64, this.area.w, this.area.h, 32, 32, 0, 0);

        this.font.drawStringWithShadow(this.title, this.area.x + 10, this.area.y + 10, 0xcccccc);

        GuiScreen screen = this.mc.currentScreen;
        GuiUtils.scissor(this.scroll.x, this.scroll.y, this.scroll.w, this.scroll.h, screen.width, screen.height);

        int i = 0;
        int h = this.scroll.scrollItemSize;

        for (T element : this.elements)
        {
            int x = this.scroll.x;
            int y = this.scroll.y + i * 20;

            boolean hovered = mouseX >= x && mouseX <= this.area.getX(1) - 10;
            hovered = hovered && mouseY >= y && mouseY < y + h;

            this.render(x, y, element, hovered);
            Gui.drawRect(x - 5, y + h - 1, x + this.area.w - 15, y + h, 0xaa181818);

            i++;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        super.draw(mouseX, mouseY, partialTicks);
    }
}