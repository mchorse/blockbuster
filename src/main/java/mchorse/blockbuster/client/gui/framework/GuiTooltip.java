package mchorse.blockbuster.client.gui.framework;

import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.utils.Area;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.MathHelper;

public class GuiTooltip
{
    public GuiElement element;
    public Tooltip tooltip;

    public void set(GuiElement element, Tooltip tooltip)
    {
        this.element = element;
        this.tooltip = tooltip;
    }

    public void draw(FontRenderer font, int width, int height)
    {
        if (this.element != null)
        {
            Area area = this.element.area;

            int x = area.getX(1) + 6;
            int y = area.getY(0.5F) - font.FONT_HEIGHT / 2;
            int w = font.getStringWidth(this.tooltip.label);

            if (this.tooltip.direction == TooltipDirection.TOP)
            {
                x = area.getX(0.5F) - w / 2;
                y = area.y - font.FONT_HEIGHT - 6;
            }
            else if (this.tooltip.direction == TooltipDirection.LEFT)
            {
                x = area.x - 6 - w;
            }
            else if (this.tooltip.direction == TooltipDirection.BOTTOM)
            {
                x = area.getX(0.5F) - w / 2;
                y = area.getY(1) + 6;
            }

            x = MathHelper.clamp(x, 6, width - w - 6);
            y = MathHelper.clamp(y, 6, height - font.FONT_HEIGHT - 6);

            Gui.drawRect(x - 3, y - 3, x + w + 3, y + font.FONT_HEIGHT + 3, 0x88000000);
            font.drawStringWithShadow(this.tooltip.label, x, y, 0xffffff);
        }
    }

    public static enum TooltipDirection
    {
        TOP, LEFT, BOTTOM, RIGHT;
    }

    public static class Tooltip
    {
        public String label;
        public TooltipDirection direction;

        public Tooltip(String label, TooltipDirection direction)
        {
            this.label = label;
            this.direction = direction;
        }
    }
}