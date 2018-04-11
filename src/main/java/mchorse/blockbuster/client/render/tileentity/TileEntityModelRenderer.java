package mchorse.blockbuster.client.render.tileentity;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
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
    public void render(TileEntityModel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (te.morph != null)
        {
            if (te.entity == null)
            {
                te.entity = new EntityActor(this.getWorld());
            }

            EntityLivingBase entity = te.entity;

            /* Apply entity rotations */
            entity.setPositionAndRotation(x, y, z, 0, 0);
            entity.rotationYawHead = entity.prevRotationYawHead = te.rotateYawHead;
            entity.rotationPitch = entity.prevRotationPitch = te.rotatePitch;
            entity.renderYawOffset = entity.prevRenderYawOffset = te.rotateBody;

            /* Apply transformations */
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5F + te.x, y + te.y, z + 0.5F + te.z);
            GlStateManager.rotate(te.rx, 1, 0, 0);
            GlStateManager.rotate(te.ry, 0, 1, 0);
            GlStateManager.rotate(te.rz, 0, 0, 1);
            GlStateManager.scale(te.sx, te.sy, te.sz);
            te.morph.render(entity, 0, 0, 0, 0, partialTicks);
            GlStateManager.popMatrix();
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