package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor;

import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.utils.ScrollArea;
import mchorse.blockbuster.client.gui.utils.ScrollArea.ScrollDirection;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.metamorph.client.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;

public class GuiRecordSelector extends GuiElement
{
    public GuiRecordingEditorPanel panel;
    public ScrollArea scroll;
    public Consumer<Action> callback;

    public int tick = -1;
    public int index = -1;

    public GuiRecordSelector(Minecraft mc, GuiRecordingEditorPanel panel, Consumer<Action> callback)
    {
        super(mc);

        this.scroll = new ScrollArea(34);
        this.scroll.direction = ScrollDirection.HORIZONTAL;
        this.panel = panel;
        this.callback = callback;
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.scroll.copy(this.area);
    }

    public void update()
    {
        if (this.panel.record != null)
        {
            this.tick = this.index = -1;
            this.scroll.setSize(this.panel.record.actions.size());
            this.scroll.clamp();
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (super.mouseClicked(mouseX, mouseY, mouseButton) || this.scroll.mouseClicked(mouseX, mouseY))
        {
            return true;
        }

        if (this.scroll.isInside(mouseX, mouseY))
        {
            int index = this.scroll.getIndex(mouseX, mouseY);
            int sub = (mouseY - this.area.y) / 20;

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

        this.scroll.mouseReleased(mouseX, mouseY);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        if (this.panel.record == null)
        {
            return;
        }

        this.scroll.drag(mouseX, mouseY);

        GuiScreen screen = this.mc.currentScreen;

        Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0x88000000);
        GuiUtils.scissor(this.area.x, this.area.y - 12, this.area.w, this.area.h + 12, screen.width, screen.height);

        int h = this.scroll.scrollItemSize;
        int index = this.scroll.scroll / h;

        for (int i = index, c = i + this.area.w / h + 2; i < c; i++)
        {
            int x = this.area.x - this.scroll.scroll + i * h;

            Gui.drawRect(x, this.area.y, x + 1, this.area.getY(1), 0x22ffffff);

            if (i == this.tick)
            {
                Gui.drawRect(x, this.area.y, x + h + 1, this.area.getY(1), 0x440088ff);
            }

            if (i % 5 == 0)
            {
                this.font.drawStringWithShadow(String.valueOf(i), x, this.area.y - 12, 0xffffff);
            }

            if (i >= 0 && i < this.panel.record.actions.size())
            {
                List<Action> actions = this.panel.record.actions.get(i);

                if (actions != null)
                {
                    int j = 0;

                    for (Action action : actions)
                    {
                        int y = this.area.y + j * 20;

                        if (i == this.tick && j == this.index)
                        {
                            Gui.drawRect(x, y, x + h + 1, y + 1, 0xffffffff);
                            Gui.drawRect(x, y + 19, x + h + 1, y + 20, 0xffffffff);
                            Gui.drawRect(x, y + 1, x + 1, y + 19, 0xffffffff);
                            Gui.drawRect(x + h, y + 1, x + h + 1, y + 19, 0xffffffff);
                        }

                        int color = MathHelper.hsvToRGB((float) action.getType() / 20F * 6, 1F, 0.75F);

                        Gui.drawRect(x, y, x + h, y + 20, color + 0x88000000);
                        this.font.drawStringWithShadow(String.valueOf(j), x + 6, y + 6, 0xffffff);

                        j++;
                    }
                }
            }
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        super.draw(mouseX, mouseY, partialTicks);

        this.scroll.drawScrollbar();
    }
}