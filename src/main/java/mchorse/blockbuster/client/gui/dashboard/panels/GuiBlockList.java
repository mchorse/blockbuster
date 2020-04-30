package mchorse.blockbuster.client.gui.dashboard.panels;

import java.util.List;
import java.util.function.Consumer;

import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.ScrollArea;
import mchorse.mclib.client.gui.utils.keys.IKey;
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
    public IKey title;

    public GuiBlockList(Minecraft mc, IKey title, Consumer<List<T>> callback)
    {
        super(mc, callback);

        this.title = title;
        this.area = new Area();
        this.scroll = new ScrollArea(20);
    }

    public abstract boolean addBlock(BlockPos pos);

    @Override
    public void resize()
    {
        super.resize();

        this.scroll.copy(this.area);
        this.scroll.y += 30;
        this.scroll.h -= 30;
    }

    @Override
    public void draw(GuiContext context)
    {
        this.area.draw(0xff333333);

        Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.y + 30, 0x44000000);
        this.font.drawStringWithShadow(this.title.get(), this.area.x + 10, this.area.y + 11, 0xcccccc);

        super.draw(context);
    }
}