package mchorse.blockbuster.client.gui.dashboard.panels.director;

import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.framework.GuiTooltip;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.utils.ScrollArea;
import mchorse.blockbuster.client.gui.utils.ScrollArea.ScrollDirection;
import mchorse.blockbuster.common.tileentity.director.Director;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.metamorph.client.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

/**
 * This GUI is responsible for drawing replays available in the 
 * director thing
 */
public class GuiReplaySelector extends GuiElement
{
    private Director director;
    private Consumer<Replay> callback;
    private ScrollArea scroll;
    private int current = -1;

    public GuiReplaySelector(Minecraft mc, Consumer<Replay> callback)
    {
        super(mc);

        this.callback = callback;
        this.scroll = new ScrollArea(40);
        this.scroll.direction = ScrollDirection.HORIZONTAL;
    }

    public void setDirector(Director director)
    {
        this.director = director;
        this.current = -1;
        this.update();
    }

    public void setReplay(Replay replay)
    {
        this.current = this.director.replays.indexOf(replay);
    }

    public void update()
    {
        this.scroll.setSize(this.director.replays.size());
        this.scroll.clamp();
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.scroll.copy(this.area);
        this.scroll.w -= 32;
        this.scroll.clamp();
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
            int size = this.director.replays.size();

            if (this.callback != null && index >= 0 && index < size && size != 0)
            {
                this.current = index;
                this.callback.accept(this.director.replays.get(index));
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int scroll)
    {
        if (super.mouseScrolled(mouseX, mouseY, scroll))
        {
            return true;
        }

        return this.scroll.mouseScroll(mouseX, mouseY, scroll);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);

        this.scroll.mouseReleased(mouseX, mouseY);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(tooltip, mouseX, mouseY, partialTicks);

        /* Background and shadows */
        Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0x88000000);
        this.drawGradientRect(this.area.x, this.area.y - 16, this.area.getX(1), this.area.y, 0x00000000, 0x88000000);

        this.scroll.drag(mouseX, mouseY);

        if (this.director != null && !this.director.replays.isEmpty())
        {
            int i = 0;
            int h = this.scroll.scrollItemSize;

            GuiScreen screen = this.mc.currentScreen;
            GuiUtils.scissor(this.scroll.x, this.scroll.y, this.scroll.w, this.scroll.h, screen.width, screen.height);

            for (Replay replay : this.director.replays)
            {
                int x = this.area.x + i * h - this.scroll.scroll + h / 2;
                boolean hover = this.scroll.isInside(mouseX, mouseY) && mouseX >= x - h / 2 && mouseX < x + h / 2;
                boolean active = i == this.current || hover;

                if (replay.morph != null)
                {
                    replay.morph.renderOnScreen(this.mc.thePlayer, x, this.area.getY(active ? 0.9F : 0.8F), active ? 32 : 24, 1);
                }
                else
                {
                    GlStateManager.color(1, 1, 1);
                    GlStateManager.enableAlpha();
                    this.mc.renderEngine.bindTexture(GuiDashboard.ICONS);
                    this.drawTexturedModalRect(x - 8, this.area.getY(0.5F) - 8, 32, active ? 16 : 0, 16, 16);
                    GlStateManager.disableAlpha();
                }

                if (hover && !replay.id.isEmpty())
                {
                    int w = this.font.getStringWidth(replay.id);

                    Gui.drawRect(x - w / 2 - 2, this.scroll.getY(0.5F) - 1, x + w / 2 + 2, this.scroll.getY(0.5F) + 9, 0x88000000);
                    this.font.drawStringWithShadow(replay.id, x - w / 2, this.scroll.getY(0.5F), 0xffffff);
                }

                i++;
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
        else
        {
            this.drawCenteredString(this.font, "Create a replay...", this.area.getX(0.5F), this.area.getY(0.5F) - 6, 0xffffff);
        }

        this.scroll.drawScrollbar();
    }
}