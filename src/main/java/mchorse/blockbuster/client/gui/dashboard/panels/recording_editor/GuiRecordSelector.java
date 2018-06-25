package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor;

import java.util.List;
import java.util.function.Consumer;

import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.utils.ScrollArea;
import mchorse.blockbuster.client.gui.utils.ScrollArea.ScrollDirection;
import mchorse.blockbuster.recording.actions.Action;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class GuiRecordSelector extends GuiElement
{
    public GuiRecordingEditorPanel panel;
    public ScrollArea scroll;
    public Consumer<Action> callback;

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

                if (this.callback != null && actions != null)
                {
                    this.callback.accept(sub >= 0 && sub < actions.size() ? actions.get(sub) : null);
                }
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

        Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0xff444444);

        int h = this.scroll.scrollItemSize;
        int index = this.scroll.scroll / h;

        for (int i = index, c = i + this.area.w / h + 2; i < c; i++)
        {
            int x = this.area.x - this.scroll.scroll + i * h;

            Gui.drawRect(x, this.area.y, x + 2, this.area.getY(1), 0xff888888);

            if (i % 5 == 0)
            {
                this.font.drawStringWithShadow(String.valueOf(i), x - 2, this.area.y - 12, 0xffffff);
            }

            if (i >= 0 && i < this.panel.record.actions.size())
            {
                List<Action> actions = this.panel.record.actions.get(i);

                if (actions != null)
                {
                    int j = 0;

                    for (Action action : actions)
                    {
                        this.font.drawStringWithShadow(String.valueOf(action.getType()), x + 4, this.area.y + 20 * j + 6, 0xffffff);

                        j++;
                    }
                }
            }
        }

        super.draw(mouseX, mouseY, partialTicks);

        this.scroll.drawScrollbar();
    }
}