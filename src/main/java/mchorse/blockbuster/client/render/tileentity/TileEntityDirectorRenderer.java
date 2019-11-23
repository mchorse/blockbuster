package mchorse.blockbuster.client.render.tileentity;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiBBModelRenderer;
import mchorse.blockbuster.common.block.BlockDirector;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class TileEntityDirectorRenderer extends TileEntitySpecialRenderer<TileEntityDirector>
{
    @Override
    public void render(TileEntityDirector te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        Minecraft mc = Minecraft.getMinecraft();

        /* Debug render (so people could find the block, lmao) */
        if (mc.gameSettings.showDebugInfo && !mc.gameSettings.hideGUI)
        {
            IBlockState state = mc.world.getBlockState(te.getPos());
            boolean playing = state.getBlock() == Blockbuster.directorBlock ? state.getValue(BlockDirector.PLAYING) : false;
            int shader = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            if (shader != 0)
            {
                OpenGlHelper.glUseProgram(0);
            }

            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();

            if (playing)
            {
                GuiBBModelRenderer.drawCube(x + 0.25F, y + 0.25F, z + 0.25F, x + 0.75F, y + 0.75F, z + 0.75F, 0, 1, 0, 0.5F);
            }
            else
            {
                GuiBBModelRenderer.drawCube(x + 0.25F, y + 0.25F, z + 0.25F, x + 0.75F, y + 0.75F, z + 0.75F, 1, 0, 0, 0.5F);
            }

            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();

            if (shader != 0)
            {
                OpenGlHelper.glUseProgram(shader);
            }
        }
    }
}