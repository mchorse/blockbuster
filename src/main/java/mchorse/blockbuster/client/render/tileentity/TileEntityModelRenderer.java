package mchorse.blockbuster.client.render.tileentity;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.RenderingHandler;
import mchorse.blockbuster.client.RenderingHandler.TEModel;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.common.tileentity.TileEntityModel.RotationOrder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;

/**
 * Model tile entity renderer
 * 
 * This class is responsible for rendering a model based on given tile 
 * entity data.
 */
public class TileEntityModelRenderer extends TileEntitySpecialRenderer<TileEntityModel>
{
    @Override
    public void renderTileEntityAt(TileEntityModel te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (te.morph != null)
        {
            if (te.entity == null)
            {
                te.createEntity();
            }

            EntityLivingBase entity = te.entity;

            /* Apply entity rotations */
            entity.setPositionAndRotation(0, 0, 0, 0, 0);
            entity.setLocationAndAngles(0, 0, 0, 0, 0);
            entity.rotationYawHead = entity.prevRotationYawHead = te.rotateYawHead;
            entity.rotationYaw = entity.prevRotationYaw = 0;
            entity.rotationPitch = entity.prevRotationPitch = te.rotatePitch;
            entity.renderYawOffset = entity.prevRenderYawOffset = te.rotateBody;
            entity.setVelocity(0, 0, 0);

            /* Apply transformations */
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5F + te.x, y + te.y, z + 0.5F + te.z);

            if (te.order == RotationOrder.ZYX)
            {
                GlStateManager.rotate(te.rx, 1, 0, 0);
                GlStateManager.rotate(te.ry, 0, 1, 0);
                GlStateManager.rotate(te.rz, 0, 0, 1);
            }
            else
            {
                GlStateManager.rotate(te.rz, 0, 0, 1);
                GlStateManager.rotate(te.ry, 0, 1, 0);
                GlStateManager.rotate(te.rx, 1, 0, 0);
            }

            if (te.one)
            {
                GlStateManager.scale(te.sx, te.sx, te.sx);
            }
            else
            {
                GlStateManager.scale(te.sx, te.sy, te.sz);
            }

            te.morph.render(entity, 0, 0, 0, 0, partialTicks);
            GlStateManager.popMatrix();

            if (!Blockbuster.proxy.config.model_block_disable_culling_workaround)
            {
                /* Stupid TEs getting culled when the chunk is getting 
                 * culled, so there is a workaround for that */
                TEModel model = RenderingHandler.models.get(te.getPos());

                if (model == null)
                {
                    RenderingHandler.models.put(te.getPos(), new TEModel(te, OpenGlHelper.lastBrightnessX, OpenGlHelper.lastBrightnessY));
                }
                else
                {
                    model.render = false;
                    model.lastX = OpenGlHelper.lastBrightnessX;
                    model.lastY = OpenGlHelper.lastBrightnessY;
                }
            }
        }

        /* Debug render (so people could find the block, lmao) */
        if (mc.gameSettings.showDebugInfo && !mc.gameSettings.hideGUI)
        {
            GlStateManager.glLineWidth(1);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.disableTexture2D();
            RenderGlobal.drawBoundingBox(x + 0.25F, y + 0.25F, z + 0.25F, x + 0.75F, y + 0.75F, z + 0.75F, 1, 1, 1, 1);
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
        }
    }
}