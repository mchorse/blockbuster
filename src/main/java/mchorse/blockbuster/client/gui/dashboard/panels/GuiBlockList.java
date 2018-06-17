package mchorse.blockbuster.client.gui.dashboard.panels;

import java.util.function.Consumer;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.framework.elements.list.GuiListElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.BlockPos;

/**
 * GUI block list
 * 
 * This GUI module is responsible for rendering and selecting 
 */
public abstract class GuiBlockList<T> extends GuiListElement<T>
{
    /**
     * Title of this panel 
     */
    public String title;

    public GuiBlockList(Minecraft mc, String title, Consumer<T> callback)
    {
        super(mc, callback);

        this.title = title;
    }

    public abstract boolean addBlock(BlockPos pos);

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.scroll.y += 30;
        this.scroll.h -= 30;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.mc.renderEngine.bindTexture(GuiDashboard.ICONS);
        net.minecraftforge.fml.client.config.GuiUtils.drawContinuousTexturedBox(this.area.x, this.area.y, 0, 64, this.area.w, this.area.h, 32, 32, 0, 0);

        Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.y + 30, 0x44000000);
        this.font.drawStringWithShadow(this.title, this.area.x + 10, this.area.y + 11, 0xcccccc);

        super.draw(mouseX, mouseY, partialTicks);
    }
}