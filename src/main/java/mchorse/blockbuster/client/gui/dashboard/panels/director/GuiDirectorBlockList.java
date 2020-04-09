package mchorse.blockbuster.client.gui.dashboard.panels.director;

import java.util.List;
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
    public GuiDirectorBlockList(Minecraft mc, Consumer<List<TileEntityDirector>> callback)
    {
        super(mc, callback);
        this.scroll.scrollItemSize = 16;
    }

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
    protected String elementToString(TileEntityDirector element, int i, int x, int y, boolean hover, boolean selected)
    {
        BlockPos pos = element.getPos();
        String label = element.director.title;

        if (label.isEmpty())
        {
            label = String.format("(%s, %s, %s)", pos.getX(), pos.getY(), pos.getZ());
        }

        return label;
    }
}