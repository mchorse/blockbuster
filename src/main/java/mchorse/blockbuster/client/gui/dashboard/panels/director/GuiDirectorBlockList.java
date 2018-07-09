package mchorse.blockbuster.client.gui.dashboard.panels.director;

import java.util.function.Consumer;

import mchorse.blockbuster.client.gui.dashboard.panels.GuiBlockList;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Model block list 
 */
public class GuiDirectorBlockList extends GuiBlockList<TileEntityDirector>
{
    public GuiDirectorBlockList(Minecraft mc, String title, Consumer<TileEntityDirector> callback)
    {
        super(mc, title, callback);
    }

    @Override
    public boolean addBlock(BlockPos pos)
    {
        TileEntity tile = this.mc.theWorld.getTileEntity(pos);

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

        this.font.drawStringWithShadow(label, x + 10, y + 6, hovered ? 16777120 : 0xffffff);
    }
}