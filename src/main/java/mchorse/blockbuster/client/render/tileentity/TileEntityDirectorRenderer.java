package mchorse.blockbuster.client.render.tileentity;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.block.BlockDirector;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class TileEntityDirectorRenderer extends TileEntitySpecialRenderer<TileEntityDirector>
{
    @Override
    public void renderTileEntityAt(TileEntityDirector te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        Minecraft mc = Minecraft.getMinecraft();

        /* Debug render (so people could find the block, lmao) */
        if (mc.gameSettings.showDebugInfo && !mc.gameSettings.hideGUI)
        {
            IBlockState state = mc.theWorld.getBlockState(te.getPos());
            boolean playing = state.getBlock() == Blockbuster.directorBlock ? state.getValue(BlockDirector.PLAYING) : false;

            GlStateManager.glLineWidth(1);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.disableTexture2D();

            if (playing)
            {
                RenderGlobal.drawBoundingBox(x + 0.25F, y + 0.25F, z + 0.25F, x + 0.75F, y + 0.75F, z + 0.75F, 0, 1, 0, 1);
            }
            else
            {
                RenderGlobal.drawBoundingBox(x + 0.25F, y + 0.25F, z + 0.25F, x + 0.75F, y + 0.75F, z + 0.75F, 1, 0, 0, 1);
            }

            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
        }
    }
}