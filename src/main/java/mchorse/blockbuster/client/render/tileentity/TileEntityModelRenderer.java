package mchorse.blockbuster.client.render.tileentity;

import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.common.tileentity.TileEntityModel.RotationOrder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

/**
 * Model tile entity renderer
 * 
 * This class is responsible for rendering a model based on given tile 
 * entity data.
 */
public class TileEntityModelRenderer extends TileEntitySpecialRenderer<TileEntityModel>
{
    /**
     * Entity shadow rendering  
     */
    public RenderShadow renderer;

    @Override
    public void renderTileEntityAt(TileEntityModel te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (te.morph != null)
        {
            if (this.renderer == null)
            {
                this.renderer = new RenderShadow(mc.getRenderManager());
            }

            if (te.entity == null)
            {
                te.createEntity();
            }

            EntityLivingBase entity = te.entity;

            /* Apply entity rotations */
            BlockPos pos = te.getPos();

            entity.setPositionAndRotation(pos.getX() + 0.5F, pos.getY() + te.y, pos.getZ() + 0.5F, 0, 0);
            entity.setLocationAndAngles(pos.getX() + 0.5F, pos.getY() + te.y, pos.getZ() + 0.5F, 0, 0);
            entity.rotationYawHead = entity.prevRotationYawHead = te.rotateYawHead;
            entity.rotationYaw = entity.prevRotationYaw = 0;
            entity.rotationPitch = entity.prevRotationPitch = te.rotatePitch;
            entity.renderYawOffset = entity.prevRenderYawOffset = te.rotateBody;
            entity.setVelocity(0, 0, 0);

            float xx = (float) x + 0.5F + te.x;
            float yy = (float) y + te.y;
            float zz = (float) z + 0.5F + te.z;

            /* Apply transformations */
            GlStateManager.pushMatrix();
            GlStateManager.translate(xx, yy, zz);

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

            // System.out.println("Test!");

            if (te.shadow)
            {
                this.renderer.setShadowSize(te.morph.getWidth(entity) * 0.8F);
                this.renderer.doRenderShadowAndFire(te.entity, xx, yy, zz, 0, partialTicks);
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

    @Override
    public boolean isGlobalRenderer(TileEntityModel te)
    {
        return true;
    }

    /**
     * Used for rendering entity shadow 
     */
    public static class RenderShadow extends Render<Entity>
    {
        protected RenderShadow(RenderManager renderManager)
        {
            super(renderManager);
        }

        @Override
        protected ResourceLocation getEntityTexture(Entity entity)
        {
            return null;
        }

        public void setShadowSize(float size)
        {
            this.shadowSize = size;
            this.shadowOpaque = 0.8F;
        }
    }
}