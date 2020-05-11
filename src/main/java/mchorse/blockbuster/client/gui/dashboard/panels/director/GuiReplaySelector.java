package mchorse.blockbuster.client.gui.dashboard.panels.director;

import mchorse.blockbuster.recording.scene.Replay;
import mchorse.blockbuster.utils.mclib.BBIcons;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

import java.util.List;
import java.util.function.Consumer;

/**
 * This GUI is responsible for drawing replays available in the 
 * director thing
 */
public class GuiReplaySelector extends GuiListElement<Replay>
{
    private String hovered;
    private int hoverX;
    private int hoverY;

    public GuiReplaySelector(Minecraft mc, Consumer<List<Replay>> callback)
    {
        super(mc, callback);

        this.horizontal().sorting();
        this.scroll.scrollItemSize = 40;
    }

    @Override
    public void draw(GuiContext context)
    {
        this.hovered = null;

        super.draw(context);

        if (this.hovered != null)
        {
            int w = this.font.getStringWidth(hovered);
            int x = this.hoverX - w / 2;

            Gui.drawRect(x - 2, this.hoverY - 1, x + w + 2, this.hoverY + 9, 0x88000000);
            this.font.drawStringWithShadow(this.hovered, x, this.hoverY, 0xffffff);
        }
        else if (this.getList().isEmpty())
        {
            this.drawCenteredString(this.font, I18n.format("blockbuster.gui.director.no_replays"), this.area.mx(), this.area.my() - 6, 0xffffff);
        }
    }

    @Override
    public void drawListElement(Replay replay, int i, int x, int y, boolean hover, boolean selected)
    {
        if (this.getDraggingIndex() != i)
        {
            x += this.scroll.scrollItemSize / 2;
        }
        else
        {
            y -= 20;
        }

        if (replay.morph != null)
        {
            replay.morph.renderOnScreen(this.mc.player, x, y + (int) (this.scroll.h * (selected ? 0.9F : 0.8F)), selected ? 32 : 24, 1);
        }
        else
        {
            GlStateManager.color(1, 1, 1);
            BBIcons.CHICKEN.render(x - 8, y + this.scroll.h / 2 - 8);
        }

        if (hover && !replay.id.isEmpty() && this.hovered == null)
        {
            this.hovered = replay.id;
            this.hoverX = x;
            this.hoverY = y + this.scroll.h / 2;
        }
    }
}