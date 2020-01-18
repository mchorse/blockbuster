package mchorse.blockbuster.client.gui.dashboard.panels.director;

import java.util.function.Consumer;

import mchorse.blockbuster.client.gui.dashboard.panels.GuiBlockList;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Model block list 
 */
public class GuiDirectorBlockList extends GuiListElement<TileEntityDirector>
{
    public GuiDirectorBlockList(Minecraft mc, Consumer<TileEntityDirector> callback)
    {
        super(mc, callback);
        this.scroll.scrollItemSize = 16;
    }

    @Override
    public void sort()
    {}

    public boolean addBlock(BlockPos pos)
    {
        TileEntity tile = this.mc.world.getTileEntity(pos);

        if (tile instanceof TileEntityDirector)
        {
            this.list.add((TileEntityDirector) tile);

            this.scroll.setSize(this.list.size());
            this.scroll.clamp();

            return true;
        }

        return false;
    }

    @Override
    public void drawElement(TileEntityDirector item, int i, int x, int y, boolean hovered)
    {
        BlockPos pos = item.getPos();
        String label = item.director.title;

        if (label.isEmpty())
        {
            label = String.format("(%s, %s, %s)", pos.getX(), pos.getY(), pos.getZ());
        }

        if (this.current == i) {
            Gui.drawRect(x, y, x + this.scroll.w, y + this.scroll.scrollItemSize, -2013230849);
        }

        this.font.drawStringWithShadow(label, (float)(x + 4), (float)(y + 4), hovered ? 16777120 : 16777215);
    }
}