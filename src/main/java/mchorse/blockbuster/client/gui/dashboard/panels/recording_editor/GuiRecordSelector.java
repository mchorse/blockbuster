package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor;

import java.util.List;
import java.util.function.Consumer;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;

import mchorse.blockbuster.recording.actions.ActionRegistry;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.ScrollArea;
import mchorse.mclib.client.gui.utils.ScrollArea.ScrollDirection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;

public class GuiRecordSelector extends GuiElement
{
    public GuiRecordingEditorPanel panel;
    public ScrollArea scroll;
    public ScrollArea vertical;
    public Consumer<Action> callback;

    public int tick = -1;
    public int index = -1;

    public boolean lastDragging = false;
    public int lastX;
    public int lastY;
    public int lastH;
    public int lastV;

    public boolean dragging;
    public boolean moving;

    public GuiRecordSelector(Minecraft mc, GuiRecordingEditorPanel panel, Consumer<Action> callback)
    {
        super(mc);

        this.scroll = new ScrollArea(34);
        this.scroll.direction = ScrollDirection.HORIZONTAL;
        this.scroll.scrollSpeed = 34;
        this.vertical = new ScrollArea(20);
        this.panel = panel;
        this.callback = callback;
    }

    @Override
    public void resize()
    {
        super.resize();

        this.scroll.copy(this.area);
        this.scroll.w -= 20;

        this.vertical.copy(this.area);
        this.vertical.w = 4;
    }

    public void update()
    {
        if (this.panel.record != null)
        {
            this.tick = this.index = -1;
            this.scroll.setSize(this.panel.record.actions.size());
            this.scroll.clamp();

            this.recalculateVertical();
        }
    }

    public void recalculateVertical()
    {
        int max = 0;

        if (this.panel.record != null)
        {
            for (List<Action> actions : this.panel.record.actions)
            {
                if (actions != null && actions.size() > max)
                {
                    max = actions.size();
                }
            }

            max += 1;
        }

        this.vertical.setSize(max);
        this.vertical.clamp();
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        this.lastX = context.mouseX;
        this.lastY = context.mouseY;

        if (context.mouseButton == 2 && this.area.isInside(context))
        {
            this.lastDragging = true;
            this.lastH = this.scroll.scroll;
            this.lastV = this.vertical.scroll;

            return true;
        }

        if (super.mouseClicked(context) || this.scroll.mouseClicked(context) || this.vertical.mouseClicked(context))
        {
            return true;
        }

        if (this.scroll.isInside(context) && !this.moving)
        {
            int index = this.scroll.getIndex(context.mouseX, context.mouseY);
            int sub = this.vertical.getIndex(context.mouseX, context.mouseY);

            if (index >= 0 && index < this.panel.record.actions.size())
            {
                List<Action> actions = this.panel.record.actions.get(index);
                boolean within = actions != null && (sub >= 0 && sub < actions.size());

                if (this.callback != null)
                {
                    this.callback.accept(actions != null && within ? actions.get(sub) : null);
                }

                this.tick = index;
                this.index = within ? sub : -1;

                if (this.index != -1)
                {
                    this.dragging = true;
                    this.moving = false;
                }
            }
            else
            {
                this.tick = -1;
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(GuiContext context)
    {
        return super.mouseScrolled(context) || this.scroll.mouseScroll(context);
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        super.mouseReleased(context);

        if (this.moving)
        {
            this.panel.moveTo(this.scroll.getIndex(context.mouseX, context.mouseY));
        }

        this.lastDragging = false;
        this.dragging = false;
        this.moving = false;
        this.scroll.mouseReleased(context);
        this.vertical.mouseReleased(context);
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.panel.record == null)
        {
            return;
        }

        int mouseX = context.mouseX;
        int mouseY = context.mouseY;

        if (this.lastDragging)
        {
            this.scroll.scroll = this.lastH + (this.lastX - mouseX);
            this.scroll.clamp();
            this.vertical.scroll = this.lastV + (this.lastY - mouseY);
            this.vertical.clamp();
        }

        if (this.dragging && !this.moving && (Math.abs(mouseX - this.lastX) > 2 || Math.abs(mouseY - this.lastY) > 2))
        {
            this.moving = true;
        }

        this.scroll.drag(mouseX, mouseY);
        this.vertical.drag(mouseX, mouseY);

        GuiScreen screen = this.mc.currentScreen;

        this.scroll.draw(0x88000000);
        GuiDraw.scissor(this.area.x, this.area.y, this.area.w, this.area.h, context);

        int h = this.scroll.scrollItemSize;
        int index = this.scroll.scroll / h;

        for (int i = index, c = i + this.area.w / h + 2; i < c; i++)
        {
            int x = this.scroll.x - this.scroll.scroll + i * h;

            Gui.drawRect(x, this.scroll.y, x + 1, this.scroll.ey(), 0x22ffffff);

            if (i == this.tick)
            {
                Gui.drawRect(x, this.scroll.y, x + h + 1, this.scroll.ey(), 0x440088ff);
            }

            if (i >= 0 && i < this.panel.record.actions.size())
            {
                List<Action> actions = this.panel.record.actions.get(i);

                if (actions != null)
                {
                    int j = 0;

                    for (Action action : actions)
                    {
                        int y = this.scroll.y + j * 20 - this.vertical.scroll;
                        int color = MathHelper.hsvToRGB((ActionRegistry.getType(action) - 1) / 20F, 1F, 1F);

                        Gui.drawRect(x, y, x + h, y + 20, color + 0x88000000);
                        this.font.drawStringWithShadow(String.valueOf(j), x + 6, y + 6, 0xffffff);

                        if (i == this.tick && j == this.index)
                        {
                            Gui.drawRect(x, y, x + h + 1, y + 1, 0xffffffff);
                            Gui.drawRect(x, y + 19, x + h + 1, y + 20, 0xffffffff);
                            Gui.drawRect(x, y + 1, x + 1, y + 19, 0xffffffff);
                            Gui.drawRect(x + h, y + 1, x + h + 1, y + 19, 0xffffffff);
                        }

                        j++;
                    }
                }
            }
        }

        GuiDraw.unscissor(context);

        if (this.moving)
        {
            int x = mouseX - h / 2;
            int y = mouseY;

            Action action = this.panel.record.getAction(this.tick, this.index);
            int color = MathHelper.hsvToRGB((ActionRegistry.getType(action) - 1) / 20F, 1F, 1F);

            Gui.drawRect(x, y, x + h, y + 20, color + 0x88000000);
            this.font.drawStringWithShadow(String.valueOf(this.index), x + 6, y + 6, 0xffffff);
        }

        for (int i = index, c = i + this.area.w / h + 2; i < c; i++)
        {
            if (i % 5 == 0)
            {
                int x = this.scroll.x - this.scroll.scroll + i * h;
                int y = this.scroll.ey() - 12;

                String str = String.valueOf(i);

                this.drawGradientRect(x + 1, y - 6, x + h, y + 12, 0x00000000, 0x88000000);
                this.font.drawStringWithShadow(str, x - this.font.getStringWidth(str) / 2 + 17, y, 0xffffff);
            }
        }

        this.scroll.drawScrollbar();
        this.vertical.drawScrollbar();

        Gui.drawRect(this.area.ex() - 20, this.area.y, this.area.ex(), this.area.ey(), 0xff222222);
        GuiDraw.drawHorizontalGradientRect(this.area.ex() - 28, this.area.y, this.area.ex() - 20, this.area.ey(), 0x00000000, 0x88000000, 0);

        super.draw(context);
    }
}