package mchorse.blockbuster.client.gui.dashboard.panels.model_block;

import java.util.function.Consumer;

import mchorse.blockbuster.client.gui.dashboard.panels.GuiBlockList;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.mclib.client.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

/**
 * Model block list 
 */
public class GuiModelBlockList extends GuiBlockList<TileEntityModel>
{
    public GuiModelBlockList(Minecraft mc, String title, Consumer<TileEntityModel> callback)
    {
        super(mc, title, callback);
    }

    @Override
    public void sort()
    {}

    @Override
    public boolean addBlock(BlockPos pos)
    {
        TileEntity tile = this.mc.theWorld.getTileEntity(pos);

        if (tile instanceof TileEntityModel)
        {
            this.list.add((TileEntityModel) tile);

            this.scroll.setSize(this.list.size());
            this.scroll.clamp();

            return true;
        }

        return false;
    }

    @Override
    public void drawElement(TileEntityModel item, int i, int x, int y, boolean hovered)
    {
        int h = this.scroll.scrollItemSize;

        if (item.morph != null)
        {
            GuiScreen screen = this.mc.currentScreen;

            int mny = MathHelper.clamp_int(y, this.scroll.y, this.scroll.getY(1));
            int mxy = MathHelper.clamp_int(y + 20, this.scroll.y, this.scroll.getY(1));

            if (mxy - mny > 0)
            {
                GuiUtils.scissor(x + this.scroll.w - 40, mny, 40, mxy - mny, screen.width, screen.height);
                item.morph.renderOnScreen(this.mc.thePlayer, x + this.scroll.w - 16, y + 30, 20, 1);
                GuiUtils.scissor(this.scroll.x, this.scroll.y, this.scroll.w, this.scroll.h, screen.width, screen.height);
            }
        }

        BlockPos pos = item.getPos();
        String label = String.format("(%s, %s, %s)", pos.getX(), pos.getY(), pos.getZ());

        this.font.drawStringWithShadow(label, x + 10, y + 6, hovered ? 16777120 : 0xffffff);
        Gui.drawRect(x, y + h - 1, x + this.area.w, y + h, 0x88181818);
    }
}