package mchorse.blockbuster.client.render;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.RenderingHandler;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * Render actor class
 *
 * Render actor entities with custom swaggalicious models ?8|
 */
public class RenderActor extends RenderLiving<EntityActor>
{
    /**
     * Default texture of the renderer
     */
    private static final ResourceLocation defaultTexture = new ResourceLocation(Blockbuster.MOD_ID, "textures/entity/actor.png");

    /**
     * Initiate render actor
     */
    public RenderActor(RenderManager manager, float shadow)
    {
        super(manager, null, shadow);
    }

    /**
     * Use skin from resource pack or default one (if skin is empty or just
     * wasn't found by actor pack)
     */
    @Override
    protected ResourceLocation getEntityTexture(EntityActor entity)
    {
        return defaultTexture;
    }

    @Override
    public boolean shouldRender(EntityActor livingEntity, ICamera camera, double camX, double camY, double camZ)
    {
        if (livingEntity.renderLast && Blockbuster.actorAlwaysRender.get() && RenderingHandler.addRenderLast(livingEntity))
        {
            return false;
        }

        if (Blockbuster.actorAlwaysRender.get())
        {
            return true;
        }

        return super.shouldRender(livingEntity, camera, camX, camY, camZ);
    }

    @Override
    public void doRender(EntityActor entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.shadowOpaque = 0;

        if (entity.invisible)
        {
            this.renderPlayerRecordingName(entity, x, y, z);

            return;
        }

        AbstractMorph morph = entity.getMorph();

        if (morph != null)
        {
            this.shadowOpaque = 1.0F;

            float shadow = 0.5F;

            if (morph instanceof CustomMorph)
            {
                CustomMorph custom = (CustomMorph) morph;

                if (custom.model != null)
                {
                    shadow = custom.getWidth(entity) * custom.model.scale[0];
                }
            }

            this.shadowSize = shadow;

            morph.render(entity, x, y, z, entityYaw, partialTicks);
        }

        this.renderLeash(entity, x, y, z, entityYaw, partialTicks);
        this.renderPlayerRecordingName(entity, x, y, z);

        if (entity.playback != null && entity.playback.record != null)
        {
            RenderingHandler.recordsToRender.add(entity.playback.record);
        }
    }

    /**
     * Renders player recording name
     */
    private void renderPlayerRecordingName(EntityActor entity, double x, double y, double z)
    {
        if (!Minecraft.getMinecraft().gameSettings.showDebugInfo)
        {
            return;
        }

        final double maxDistance = 64;
        double d0 = entity.getDistanceSq(this.renderManager.renderViewEntity);

        if (d0 <= (maxDistance * maxDistance) && entity.playback != null && entity.playback.record != null)
        {
            float viewerYaw = this.renderManager.playerViewY;
            float viewerPitch = this.renderManager.playerViewX;
            boolean isThirdPersonFrontal = this.renderManager.options.thirdPersonView == 2;
            float f2 = entity.height / 2;
            String str = entity.playback.record.filename;
            FontRenderer fontRendererIn = this.getFontRendererFromRenderManager();
            int verticalShift = -fontRendererIn.FONT_HEIGHT / 2;

            y += f2;

            int shader = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            if (shader != 0)
            {
                OpenGlHelper.glUseProgram(0);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-0.025F, -0.025F, 0.025F);
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            int i = fontRendererIn.getStringWidth(str) / 2;
            GlStateManager.disableTexture2D();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder vertexbuffer = tessellator.getBuffer();
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            vertexbuffer.pos((double)(-i - 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            vertexbuffer.pos((double)(-i - 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            vertexbuffer.pos((double)(i + 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            vertexbuffer.pos((double)(i + 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();

            fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, -1);

            GlStateManager.enableDepth();
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();

            if (shader != 0)
            {
                OpenGlHelper.glUseProgram(shader);
            }
        }
    }

    /**
     * Renderer factory
     *
     * Some interface provided by Minecraft Forge that will pass a RenderManager
     * instance into the method for easier Renders initiation.
     */
    public static class FactoryActor implements IRenderFactory<EntityActor>
    {
        @Override
        public RenderActor createRenderFor(RenderManager manager)
        {
            return new RenderActor(manager, 0.5F);
        }
    }
}