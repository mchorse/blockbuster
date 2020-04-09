package mchorse.blockbuster.client.gui.dashboard.panels.model_block;

import mchorse.blockbuster.client.gui.dashboard.panels.GuiBlockList;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.function.Consumer;

/**
 * Model block list 
 */
public class GuiModelBlockList extends GuiBlockList<TileEntityModel>
{
    public GuiModelBlockList(Minecraft mc, String title, Consumer<List<TileEntityModel>> callback)
    {
        super(mc, title, callback);
    }

    @Override
    public boolean addBlock(BlockPos pos)
    {
        TileEntity tile = this.mc.world.getTileEntity(pos);

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
    protected void drawElementPart(TileEntityModel element, int i, int x, int y, boolean hover, boolean selected)
    {
        GuiContext context = GuiBase.getCurrent();
        int h = this.scroll.scrollItemSize;

        if (element.morph != null)
        {
            GuiScreen screen = this.mc.currentScreen;

            int mny = MathHelper.clamp(y, this.scroll.y, this.scroll.ey());
            int mxy = MathHelper.clamp(y + 20, this.scroll.y, this.scroll.ey());

            if (mxy - mny > 0)
            {
                GuiDraw.scissor(x + this.scroll.w - 40, mny, 40, mxy - mny, context);
                element.morph.renderOnScreen(this.mc.player, x + this.scroll.w - 16, y + 30, 20, 1);
                GuiDraw.unscissor(context);
            }
        }

        BlockPos pos = element.getPos();
        String label = String.format("(%s, %s, %s)", pos.getX(), pos.getY(), pos.getZ());

        this.font.drawStringWithShadow(label, x + 10, y + 6, hover ? 16777120 : 0xffffff);
        Gui.drawRect(x, y + h - 1, x + this.area.w, y + h, 0x88181818);
    }
}