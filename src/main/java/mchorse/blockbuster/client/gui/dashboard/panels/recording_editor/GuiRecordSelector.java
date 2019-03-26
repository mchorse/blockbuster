package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor;

import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.recording.ActionRegistry;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.GuiUtils;
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

        this.createChildren();
        this.scroll = new ScrollArea(34);
        this.scroll.direction = ScrollDirection.HORIZONTAL;
        this.scroll.scrollSpeed = 34;
        this.vertical = new ScrollArea(20);
        this.panel = panel;
        this.callback = callback;
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

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
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.lastX = mouseX;
        this.lastY = mouseY;

        if (mouseButton == 2 && this.area.isInside(mouseX, mouseY))
        {
            this.lastDragging = true;
            this.lastH = this.scroll.scroll;
            this.lastV = this.vertical.scroll;

            return true;
        }

        if (super.mouseClicked(mouseX, mouseY, mouseButton) || this.scroll.mouseClicked(mouseX, mouseY) || this.vertical.mouseClicked(mouseX, mouseY))
        {
            return true;
        }

        if (this.scroll.isInside(mouseX, mouseY) && !this.moving)
        {
            int index = this.scroll.getIndex(mouseX, mouseY);
            int sub = this.vertical.getIndex(mouseX, mouseY);

            if (index >= 0 && index < this.panel.record.actions.size())
            {
                List<Action> actions = this.panel.record.actions.get(index);
                boolean within = actions == null ? false : sub >= 0 && sub < actions.size();

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
    public boolean mouseScrolled(int mouseX, int mouseY, int scroll)
    {
        return super.mouseScrolled(mouseX, mouseY, scroll) || this.scroll.mouseScroll(mouseX, mouseY, scroll);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);

        if (this.moving)
        {
            this.panel.moveTo(this.scroll.getIndex(mouseX, mouseY));
        }

        this.lastDragging = false;
        this.dragging = false;
        this.moving = false;
        this.scroll.mouseReleased(mouseX, mouseY);
        this.vertical.mouseReleased(mouseX, mouseY);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        if (this.panel.record == null)
        {
            return;
        }

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

        Gui.drawRect(this.scroll.x, this.scroll.y, this.scroll.getX(1), this.scroll.getY(1), 0x88000000);
        GuiUtils.scissor(this.area.x, this.area.y, this.area.w, this.area.h, screen.width, screen.height);

        int h = this.scroll.scrollItemSize;
        int index = this.scroll.scroll / h;

        for (int i = index, c = i + this.area.w / h + 2; i < c; i++)
        {
            int x = this.scroll.x - this.scroll.scroll + i * h;

            Gui.drawRect(x, this.scroll.y, x + 1, this.scroll.getY(1), 0x22ffffff);

            if (i == this.tick)
            {
                Gui.drawRect(x, this.scroll.y, x + h + 1, this.scroll.getY(1), 0x440088ff);
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

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

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
                int y = this.scroll.getY(1) - 12;

                String str = String.valueOf(i);

                this.drawGradientRect(x + 1, y - 6, x + h, y + 12, 0x00000000, 0x88000000);
                this.font.drawStringWithShadow(str, x - this.font.getStringWidth(str) / 2 + 17, y, 0xffffff);
            }
        }

        this.scroll.drawScrollbar();
        this.vertical.drawScrollbar();
        this.mc.renderEngine.bindTexture(GuiDashboard.GUI_ICONS);
        net.minecraftforge.fml.client.config.GuiUtils.drawContinuousTexturedBox(this.area.getX(1) - 20, this.area.y, 0, 64, 20, this.area.h, 32, 32, 0, 0);
        mchorse.blockbuster.client.gui.utils.GuiUtils.drawHorizontalGradientRect(this.area.getX(1) - 28, this.area.y, this.area.getX(1) - 20, this.area.getY(1), 0x00000000, 0x88000000, 0);

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }
}