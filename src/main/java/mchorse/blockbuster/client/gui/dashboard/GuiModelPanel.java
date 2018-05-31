package mchorse.blockbuster.client.gui.dashboard;

import mchorse.blockbuster.client.gui.framework.GuiElement;
import net.minecraft.client.Minecraft;

public class GuiModelPanel extends GuiElement
{
    public GuiModelPanel(Minecraft mc)
    {
        super(mc);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.font.drawStringWithShadow("This is model block panel!", this.area.x + this.area.w / 2, this.area.y + 10, 0xffffff);
    }
}