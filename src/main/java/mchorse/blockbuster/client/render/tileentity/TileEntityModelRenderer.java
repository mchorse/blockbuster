package mchorse.blockbuster.client.render.tileentity;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.common.tileentity.TileEntityModel.RotationOrder;
import mchorse.mclib.client.Draw;
import mchorse.mclib.utils.MatrixUtils;
import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

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
    public void render(TileEntityModel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (!te.morph.isEmpty() && !Blockbuster.modelBlockDisableRendering.get() && te.enabled)
        {
            AbstractMorph morph = te.morph.get();

            if (this.renderer == null)
            {
                this.renderer = new RenderShadow(mc.getRenderManager());
            }

            if (te.entity == null)
            {
                te.createEntity(mc.world);
            }

            if (te.entity == null)
            {
                return;
            }

            EntityLivingBase entity = te.entity;

            if (EntityUtils.getMorph(entity) != null)
            {
                morph = EntityUtils.getMorph(entity);
            }

            /* Apply entity rotations */
            BlockPos pos = te.getPos();

            entity.setPositionAndRotation(pos.getX() + 0.5F + te.x, pos.getY() + te.y, pos.getZ() + 0.5F + te.z, 0, 0);
            entity.setLocationAndAngles(pos.getX() + 0.5F + te.x, pos.getY() + te.y, pos.getZ() + 0.5F + te.z, 0, 0);
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
            GlStateManager.translate(x + 0.5, y, z + 0.5);
            GlStateManager.translate(te.x, te.y, te.z);

            boolean wasSet = MatrixUtils.captureMatrix();

            this.transform(te);

            MorphUtils.render(morph, entity, 0, 0, 0, 0, partialTicks);
            GlStateManager.popMatrix();

            if (te.shadow)
            {
                this.renderer.setShadowSize(morph.getWidth(entity) * 0.8F);
                this.renderer.doRenderShadowAndFire(te.entity, xx, yy, zz, 0, partialTicks);
            }

            if (wasSet) MatrixUtils.releaseMatrix();
        }

        /* Debug render (so people could find the block, lmao) */
        if (mc.gameSettings.showDebugInfo && (!mc.gameSettings.hideGUI || Blockbuster.modelBlockRenderDebuginf1.get()))
        {
            int shader = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            if (shader != 0)
            {
                OpenGlHelper.glUseProgram(0);
            }

            GlStateManager.disableTexture2D();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

            float r = te.enabled ? 0 : 1;
            float g = te.enabled ? 0.5F : 0.85F;
            float b = te.enabled ? 1 : 0;

            if (!te.morph.isEmpty() && te.morph.get().errorRendering)
            {
                r = 1;
                g = b = 0;
            }

            Draw.cube(buffer, x + 0.25F, y + 0.25F, z + 0.25F, x + 0.75F, y + 0.75F, z + 0.75F, r, g, b, 0.35F);
            Draw.cube(buffer, x + 0.45F + te.x, y + te.y, z + 0.45F + te.z, x + 0.55F + te.x, y + 0.1F + te.y, z + 0.55F + te.z, 1, 1, 1, 0.85F);

            double distance = MathHelper.sqrt(Vec3d.ZERO.squareDistanceTo(te.x, te.y, te.z));

            if (distance > 0.1)
            {
                Draw.cube(buffer, x + 0.45F, y, z + 0.45F, x + 0.55F, y + 0.1F, z + 0.55F, 1, 1, 1, 0.85F);

                tessellator.draw();

                double horizontalDistance = MathHelper.sqrt(te.x * te.x + te.z * te.z);
                double yaw = 180 - MathHelper.atan2(te.z, te.x) * 180 / Math.PI + 90;
                double pitch = MathHelper.atan2(te.y, horizontalDistance) * 180 / Math.PI;

                GL11.glPushMatrix();
                GL11.glTranslated(x + 0.5, y + 0.05F, z + 0.5);
                GL11.glRotated(yaw, 0.0F, 1.0F, 0.0F);
                GL11.glRotated(pitch, 1.0F, 0.0F, 0.0F);

                Draw.cube(-0.025F, -0.025F, 0, 0.025F, 0.025F, -distance, 0, 0, 0, 0.5F);

                GL11.glPopMatrix();
            }
            else
            {
                tessellator.draw();
            }

            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();

            if (shader != 0)
            {
                OpenGlHelper.glUseProgram(shader);
            }
        }
    }

    @Override
    public boolean isGlobalRenderer(TileEntityModel te)
    {
        return te.global;
    }

    public void transform(TileEntityModel te)
    {
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