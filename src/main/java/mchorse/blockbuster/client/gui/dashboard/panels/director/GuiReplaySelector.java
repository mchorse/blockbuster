package mchorse.blockbuster.client.gui.dashboard.panels.director;

import java.util.function.Consumer;

import mchorse.blockbuster.recording.scene.Scene;
import mchorse.blockbuster.utils.mclib.BBIcons;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;

import mchorse.blockbuster.recording.scene.Replay;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.ScrollArea;
import mchorse.mclib.client.gui.utils.ScrollArea.ScrollDirection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

/**
 * This GUI is responsible for drawing replays available in the 
 * director thing
 */
public class GuiReplaySelector extends GuiElement
{
    private Scene scene;
    private Consumer<Replay> callback;
    public ScrollArea scroll;
    public int current = -1;

    public GuiReplaySelector(Minecraft mc, Consumer<Replay> callback)
    {
        super(mc);

        this.callback = callback;
        this.scroll = new ScrollArea(40);
        this.scroll.direction = ScrollDirection.HORIZONTAL;
    }

    public void setScene(Scene scene)
    {
        this.scene = scene;
        this.current = -1;
        this.update();
    }

    public void setReplay(Replay replay)
    {
        if (this.scene != null)
        {
            this.current = this.scene.replays.indexOf(replay);
        }
    }

    public void update()
    {
        this.scroll.setSize(this.scene.replays.size());
        this.scroll.clamp();
    }

    @Override
    public void resize()
    {
        super.resize();

        this.scroll.copy(this.area);
        this.scroll.w -= 32;
        this.scroll.clamp();
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context) || this.scroll.mouseClicked(context))
        {
            return true;
        }

        if (this.scroll.isInside(context))
        {
            int index = this.scroll.getIndex(context.mouseX, context.mouseY);
            int size = this.scene.replays.size();

            if (this.callback != null && index >= 0 && index < size)
            {
                this.current = index;
                this.callback.accept(this.scene.replays.get(index));
            }

            return true;
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

        return this.scroll.mouseScroll(context);
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        super.mouseReleased(context);

        this.scroll.mouseReleased(context);
    }

    @Override
    public void draw(GuiContext context)
    {
        super.draw(context);

        /* Background and shadows */
        Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.ey(), 0x88000000);
        this.drawGradientRect(this.area.x, this.area.y - 16, this.area.ex(), this.area.y, 0x00000000, 0x88000000);

        this.scroll.drag(context);

        if (this.scene != null && !this.scene.replays.isEmpty())
        {
            int i = 0;
            int h = this.scroll.scrollItemSize;
            int hoverX = -1;
            String hovered = null;

            GuiDraw.scissor(this.scroll.x, this.scroll.y, this.scroll.w, this.scroll.h, context);

            for (Replay replay : this.scene.replays)
            {
                int x = this.area.x + i * h - this.scroll.scroll + h / 2;
                boolean hover = this.scroll.isInside(context) && context.mouseX >= x - h / 2 && context.mouseX < x + h / 2;
                boolean active = i == this.current || hover;

                if (replay.morph != null)
                {
                    replay.morph.renderOnScreen(this.mc.player, x, this.area.y(active ? 0.9F : 0.8F), active ? 32 : 24, 1);
                }
                else
                {
                    GlStateManager.color(1, 1, 1);
                    BBIcons.CHICKEN.render(x - 8, this.area.my() - 8);
                }

                if (hover && !replay.id.isEmpty() && hovered == null)
                {
                    hovered = replay.id;
                    hoverX = x;
                }

                i++;
            }

            if (hovered != null)
            {
                int w = this.font.getStringWidth(hovered);
                int x = hoverX - w / 2;

                Gui.drawRect(x - 2, this.scroll.my() - 1, x + w + 2, this.scroll.my() + 9, 0x88000000);
                this.font.drawStringWithShadow(hovered, x, this.scroll.my(), 0xffffff);
            }

            GuiDraw.unscissor(context);
        }
        else
        {
            this.drawCenteredString(this.font, I18n.format("blockbuster.gui.director.no_replays"), this.area.mx(), this.area.my() - 6, 0xffffff);
        }

        this.scroll.drawScrollbar();
    }
}