package mchorse.blockbuster.client.render.tileentity;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.gui.dashboard.panels.model_block.GuiModelBlockPanel;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.input.GuiTransformations;
import mchorse.mclib.utils.MatrixUtils.RotationOrder;
import mchorse.blockbuster.common.tileentity.TileEntityModelSettings;
import mchorse.mclib.client.Draw;
import mchorse.mclib.utils.MatrixUtils;
import mchorse.mclib.utils.RenderingUtils;
import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.bodypart.GuiBodyPartEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import javax.vecmath.Vector3d;
import java.util.List;

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
        TileEntityModelSettings teSettings = te.getSettings();

        if (!te.morph.isEmpty() && !Blockbuster.modelBlockDisableRendering.get() && teSettings.isEnabled())
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

            entity.setPositionAndRotation(pos.getX() + 0.5F + teSettings.getX(), pos.getY() + teSettings.getY(), pos.getZ() + 0.5F + teSettings.getZ(), 0, 0);
            entity.setLocationAndAngles(pos.getX() + 0.5F + teSettings.getX(), pos.getY() + teSettings.getY(), pos.getZ() + 0.5F + teSettings.getZ(), 0, 0);
            entity.rotationYawHead = entity.prevRotationYawHead = teSettings.getRotateYawHead();
            entity.rotationYaw = entity.prevRotationYaw = 0;
            entity.rotationPitch = entity.prevRotationPitch = teSettings.getRotatePitch();
            entity.renderYawOffset = entity.prevRenderYawOffset = teSettings.getRotateBody();
            entity.setVelocity(0, 0, 0);

            float xx = (float) x + 0.5F + teSettings.getX();
            float yy = (float) y + teSettings.getY();
            float zz = (float) z + 0.5F + teSettings.getZ();

            /* Apply transformations */
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5, y, z + 0.5);
            GlStateManager.translate(teSettings.getX(), teSettings.getY(), teSettings.getZ());

            boolean wasSet = MatrixUtils.captureMatrix();

            this.transform(te);

            MorphUtils.render(morph, entity, 0, 0, 0, 0, partialTicks);

            this.drawAxis(te);

            GlStateManager.popMatrix();

            if (teSettings.isShadow())
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

            float r = teSettings.isEnabled() ? 0 : 1;
            float g = teSettings.isEnabled() ? 0.5F : 0.85F;
            float b = teSettings.isEnabled() ? 1 : 0;

            if (!te.morph.isEmpty() && te.morph.get().errorRendering)
            {
                r = 1;
                g = b = 0;
            }

            Draw.cube(buffer, x + 0.25F, y + 0.25F, z + 0.25F, x + 0.75F, y + 0.75F, z + 0.75F, r, g, b, 0.35F);
            Draw.cube(buffer, x + 0.45F + teSettings.getX(), y + teSettings.getY(), z + 0.45F + teSettings.getZ(), x + 0.55F + teSettings.getX(), y + 0.1F + teSettings.getY(), z + 0.55F + teSettings.getZ(), 1, 1, 1, 0.85F);

            double distance = MathHelper.sqrt(Vec3d.ZERO.squareDistanceTo(teSettings.getX(), teSettings.getY(), teSettings.getZ()));

            if (distance > 0.1)
            {
                Draw.cube(buffer, x + 0.45F, y, z + 0.45F, x + 0.55F, y + 0.1F, z + 0.55F, 1, 1, 1, 0.85F);

                tessellator.draw();

                double horizontalDistance = MathHelper.sqrt(teSettings.getX() * teSettings.getX() + teSettings.getZ() * teSettings.getZ());
                double yaw = 180 - MathHelper.atan2(teSettings.getZ(), teSettings.getX()) * 180 / Math.PI + 90;
                double pitch = MathHelper.atan2(teSettings.getY(), horizontalDistance) * 180 / Math.PI;

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

            if (teSettings.getLightValue() != 0)
            {
                FontRenderer font = Minecraft.getMinecraft().fontRenderer;
                String text = I18n.format("blockbuster.light", teSettings.getLightValue());
                RenderManager manager = mc.getRenderManager();

                boolean isInventory = false;

                float yaw = (isInventory) ? 180F : manager.playerViewY;
                float pitch = (isInventory) ? 0F : manager.playerViewX;


                EntityRenderer.drawNameplate(this.getFontRenderer(), text, (float) (x + 0.5F), (float) (y + 0.5F) + font.FONT_HEIGHT / 48.0F + 0.05F, (float) (z + 0.5F), 0, yaw, pitch, mc.gameSettings.thirdPersonView == 2, false);
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

    private void drawAxis(TileEntityModel te)
    {
        List<GuiModelBlockPanel> childList = GuiBase.getCurrentChildren(GuiModelBlockPanel.class);

        if (childList == null) return;

        GuiModelBlockPanel modelBlockPanel = childList.get(0);

        if (modelBlockPanel != null && modelBlockPanel.isOpened() && modelBlockPanel.isSelected(te))
        {
            GlStateManager.pushMatrix();
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.disableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.disableTexture2D();

            GlStateManager.disableDepth();
            GlStateManager.disableLighting();

            Draw.point(0, 0, 0);

            if (GuiTransformations.GuiStaticTransformOrientation.getOrientation() == GuiTransformations.TransformOrientation.GLOBAL)
            {
                TileEntityModelSettings teSettings = te.getSettings();
                Vector3d rotation = new Vector3d(Math.toRadians(teSettings.getRx()), Math.toRadians(teSettings.getRy()), Math.toRadians(teSettings.getRz()));
                Vector3d scale = new Vector3d(teSettings.getSx(), teSettings.getSy(), teSettings.getSz());

                RenderingUtils.glRevertRotationScale(rotation, scale, teSettings.getOrder());
            }

            Draw.axis(0.25F);

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();

            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean isGlobalRenderer(TileEntityModel te)
    {
        return te.getSettings().isGlobal();
    }

    public void transform(TileEntityModel te)
    {
        TileEntityModelSettings teSettings = te.getSettings();

        if (teSettings.getOrder() == RotationOrder.ZYX)
        {
            GlStateManager.rotate(teSettings.getRx(), 1, 0, 0);
            GlStateManager.rotate(teSettings.getRy(), 0, 1, 0);
            GlStateManager.rotate(teSettings.getRz(), 0, 0, 1);
        }
        else
        {
            GlStateManager.rotate(teSettings.getRz(), 0, 0, 1);
            GlStateManager.rotate(teSettings.getRy(), 0, 1, 0);
            GlStateManager.rotate(teSettings.getRx(), 1, 0, 0);
        }

        if (teSettings.isUniform())
        {
            GlStateManager.scale(teSettings.getSx(), teSettings.getSx(), teSettings.getSx());
        }
        else
        {
            GlStateManager.scale(teSettings.getSx(), teSettings.getSy(), teSettings.getSz());
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