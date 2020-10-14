package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor;

import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.ActionRegistry;
import mchorse.blockbuster.recording.actions.MorphAction;
import mchorse.blockbuster_pack.morphs.SequencerMorph;
import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.ScrollArea;
import mchorse.mclib.client.gui.utils.ScrollArea.ScrollDirection;
import mchorse.mclib.utils.MathUtils;
import mchorse.metamorph.api.morphs.utils.Animation;
import mchorse.metamorph.api.morphs.utils.IAnimationProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.function.Consumer;

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

    private int adaptiveMaxIndex;

    public GuiRecordSelector(Minecraft mc, GuiRecordingEditorPanel panel, Consumer<Action> callback)
    {
        super(mc);

        this.scroll = new ScrollArea(34);
        this.scroll.direction = ScrollDirection.HORIZONTAL;
        this.scroll.scrollSpeed = 34 * 2;
        this.vertical = new ScrollArea(20);
        this.panel = panel;
        this.callback = callback;
    }

    public void reset()
    {}

    @Override
    public void resize()
    {
        super.resize();

        this.scroll.copy(this.area);
        this.vertical.copy(this.area);
    }

    public void update()
    {
        if (this.panel.record != null)
        {
            int count = this.panel.record.actions.size();

            this.tick = MathUtils.clamp(this.tick, 0, count - 1);
            this.index = -1;

            this.scroll.setSize(count);
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
        if (super.mouseScrolled(context))
        {
            return true;
        }

        boolean shift = GuiScreen.isShiftKeyDown();
        boolean alt = GuiScreen.isAltKeyDown();

        if (shift && !alt)
        {
            return this.vertical.mouseScroll(context);
        }
        else if (alt && !shift)
        {
            int scale = this.scroll.scrollItemSize;

            this.scroll.scrollItemSize = MathUtils.clamp(this.scroll.scrollItemSize + (int) Math.copySign(2, context.mouseWheel), 6, 50);
            this.scroll.setSize(this.panel.record.actions.size());
            this.scroll.clamp();

            if (this.scroll.scrollItemSize != scale)
            {
                int value = this.scroll.scroll + (context.mouseX - this.area.x);

                this.scroll.scroll = (int) ((value - (value - this.scroll.scroll) * (scale / (float) this.scroll.scrollItemSize)) * (this.scroll.scrollItemSize / (float) scale));
            }

            return true;
        }

        return this.scroll.mouseScroll(context);
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
        int count = this.panel.record.actions.size();

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
        this.scroll.draw(0x88000000);

        Gui.drawRect(this.area.ex(), this.area.y, this.area.ex() + 20, this.area.ey(), 0xff222222);
        Gui.drawRect(this.area.x - 20, this.area.y, this.area.x, this.area.ey(), 0xff222222);
        GuiDraw.drawHorizontalGradientRect(this.area.ex() - 8, this.area.y, this.area.ex(), this.area.ey(), 0x00000000, 0x88000000, 0);
        GuiDraw.drawHorizontalGradientRect(this.area.x, this.area.y, this.area.x + 8, this.area.ey(), 0x88000000, 0x00000000, 0);

        int max = this.area.x + this.scroll.scrollItemSize * count;

        if (max < this.area.ex())
        {
            Gui.drawRect(max, this.area.y, this.area.ex(), this.area.ey(), 0xaa000000);
        }

        GuiDraw.scissor(this.area.x, this.area.y, this.area.w, this.area.h, context);

        int w = this.scroll.scrollItemSize;
        int index = this.scroll.scroll / w;
        int diff = index;

        index -= this.adaptiveMaxIndex;
        index = index < 0 ? 0 : index;
        diff = diff - index;

        this.adaptiveMaxIndex = 0;

        for (int i = index, c = i + this.area.w / w + 2 + diff; i < c; i++)
        {
            int x = this.scroll.x - this.scroll.scroll + i * w;

            if (i < count)
            {
                Gui.drawRect(x, this.scroll.y, x + 1, this.scroll.ey(), 0x22ffffff);
            }

            if (i == this.tick)
            {
                Gui.drawRect(x, this.scroll.y, x + w + 1, this.scroll.ey(), 0x440088ff);
            }

            if (i >= 0 && i < count)
            {
                List<Action> actions = this.panel.record.actions.get(i);

                if (actions != null)
                {
                    int j = 0;

                    for (Action action : actions)
                    {
                        String label = String.valueOf(j);

                        int y = this.scroll.y + j * 20 - this.vertical.scroll;
                        int color = MathHelper.hsvToRGB((ActionRegistry.getType(action) - 1) / 20F, 1F, 1F);
                        boolean selected = i == this.tick && j == this.index;
                        int offset = this.scroll.scrollItemSize < 18 ? (this.scroll.scrollItemSize - this.font.getStringWidth(label)) / 2 : 6;

                        if (!this.moving || !selected)
                        {
                            this.drawAnimationLength(action, x, y, color, selected);
                        }

                        Gui.drawRect(x, y, x + w, y + 20, color + 0x88000000);
                        this.font.drawStringWithShadow(label, x + offset, y + 6, 0xffffff);

                        if (selected)
                        {
                            GuiDraw.drawOutline(x, y, x + w, y + 20, 0xffffffff);
                        }

                        j++;
                    }
                }
            }
        }

        for (int i = index, c = i + this.area.w / w + 2; i < c; i++)
        {
            if (i % 5 == 0 && i < count)
            {
                int x = this.scroll.x - this.scroll.scroll + i * w;
                int y = this.scroll.ey() - 12;

                String str = String.valueOf(i);

                this.drawGradientRect(x + 1, y - 6, x + w, y + 12, 0x00000000, 0x88000000);
                this.font.drawStringWithShadow(str, x + (this.scroll.scrollItemSize - this.font.getStringWidth(str) + 2) / 2, y, 0xffffff);
            }
        }

        this.scroll.drawScrollbar();
        this.vertical.drawScrollbar();

        String label = this.panel.record.filename;

        GuiDraw.drawTextBackground(this.font, label, this.area.ex() - this.font.getStringWidth(label) - 5, this.area.ey() - 13, 0xffffff, 0xaa000000 + McLib.primaryColor.get());

        GuiDraw.unscissor(context);

        if (this.moving)
        {
            int x = mouseX - w / 2;
            int y = mouseY;

            Action action = this.panel.record.getAction(this.tick, this.index);
            int color = MathHelper.hsvToRGB((ActionRegistry.getType(action) - 1) / 20F, 1F, 1F);

            this.drawAnimationLength(action, x, y, color, true);

            Gui.drawRect(x, y, x + w, y + 20, color + 0x88000000);
            this.font.drawStringWithShadow(String.valueOf(this.index), x + 6, y + 6, 0xffffff);
            GuiDraw.drawOutline(x, y, x + w, y + 20, 0xffffffff);
        }

        super.draw(context);
    }

    private void drawAnimationLength(Action action, int x, int y, int color, boolean selected)
    {
        if (action instanceof MorphAction)
        {
            MorphAction morphAction = (MorphAction) action;
            int ticks = 0;

            if (morphAction.morph instanceof IAnimationProvider)
            {
                Animation animation = ((IAnimationProvider) morphAction.morph).getAnimation();

                if (animation.animates)
                {
                    ticks = animation.duration;
                }
            }
            else if (morphAction.morph instanceof SequencerMorph)
            {
                SequencerMorph morph = (SequencerMorph) morphAction.morph;

                ticks = (int) morph.getDuration();
            }

            if (ticks > 0)
            {
                int offset = x + this.scroll.scrollItemSize;

                Gui.drawRect(offset, y + 8, offset + ticks * this.scroll.scrollItemSize, y + 12, selected ? 0xffffffff : color + 0x33000000);
            }

            this.adaptiveMaxIndex = Math.max(ticks, this.adaptiveMaxIndex);
        }
    }
}