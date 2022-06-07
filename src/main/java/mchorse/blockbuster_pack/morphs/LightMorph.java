package mchorse.blockbuster_pack.morphs;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.textures.GifTexture;
import mchorse.blockbuster.common.block.BlockModel;
import mchorse.blockbuster.common.entity.ExpirableDummyEntity;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.render.VertexBuilder;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.Interpolation;
import mchorse.mclib.utils.Interpolations;
import mchorse.mclib.utils.MathUtils;
import mchorse.mclib.utils.MatrixUtils;
import mchorse.mclib.utils.ReflectionUtils;
import mchorse.mclib.utils.RenderingUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.utils.Animation;
import mchorse.metamorph.api.morphs.utils.IAnimationProvider;
import mchorse.metamorph.api.morphs.utils.IMorphGenerator;
import mchorse.metamorph.api.morphs.utils.ISyncableMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.SingularMatrixException;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import java.nio.FloatBuffer;
import java.util.Objects;

public class LightMorph extends AbstractMorph implements IAnimationProvider, ISyncableMorph, IMorphGenerator
{
    private LightAnimation animation = new LightAnimation();
    private LightProperties lightProperties = new LightProperties();
    private int light = 15;
    private ExpirableDummyEntity dummy;
    private Vector3f position = new Vector3f();
    private Vector3f prevPosition = new Vector3f();
    private boolean renderedOnScreen;
    private boolean renderedInEditor;

    /** The age when it last rendered */
    private int lastRenderAge;

    public LightMorph()
    {
        super();

        this.name = "light";
    }

    public void setLightValue(int light)
    {
        this.light = MathUtils.clamp(light, 0, 15);
    }

    public int getLightValue()
    {
        return this.light;
    }

    @Override
    public Animation getAnimation()
    {
        return this.animation;
    }

    @Override
    public boolean isPaused()
    {
        return this.animation.paused;
    }

    @Override
    public boolean canGenerate()
    {
        return this.animation.isInProgress();
    }

    @Override
    public void update(EntityLivingBase target)
    {
        if (target.world.isRemote && !this.renderedOnScreen && !this.renderedInEditor)
        {
            this.addDummyEntityToWorld();
        }

        this.animation.update();

        super.update(target);
    }

    @SideOnly(Side.CLIENT)
    private void updateDummyEntity()
    {
        if (this.dummy == null)
        {
            return;
        }

        this.dummy.setLifetime(this.dummy.getAge() + Math.abs(this.dummy.getAge() - this.lastRenderAge) + 2);

        this.updateDummyEntityPosition();

        this.dummy.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(BlockModel.getItemFromMeta(this.lightProperties.lightValue)));

        this.prevPosition.set(this.position);
    }

    @SideOnly(Side.CLIENT)
    private void addDummyEntityToWorld()
    {
        if (this.dummy == null || this.dummy.isDead)
        {
            this.dummy = new ExpirableDummyEntity(Minecraft.getMinecraft().world, 1,1,1);

            this.updateDummyEntityPosition();

            Minecraft.getMinecraft().world.addEntityToWorld(this.dummy.getEntityId(), this.dummy);
        }
    }

    private void updateDummyEntityPosition()
    {
        this.dummy.prevPosX = this.prevPosition.x;
        this.dummy.prevPosY = this.prevPosition.y;
        this.dummy.prevPosZ = this.prevPosition.z;
        this.dummy.lastTickPosX = this.prevPosition.x;
        this.dummy.lastTickPosY = this.prevPosition.y;
        this.dummy.lastTickPosZ = this.prevPosition.z;

        this.dummy.setPosition(this.position.x, this.position.y, this.position.z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer entityPlayer, int x, int y, float scale, float alpha)
    {
        float partial = Minecraft.getMinecraft().getRenderPartialTicks();

        this.updateAnimation(partial);

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y - scale / 2, 0);
        GL11.glScalef(1.5F, -1.5F, 1.5F);

        this.renderPictureTexture(scale, partial);

        GL11.glPopMatrix();

        this.renderedOnScreen = true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entityLivingBase, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (ReflectionUtils.isOptifineShadowPass())
        {
            return;
        }

        this.updateAnimation(partialTicks);

        Matrix4d[] transformation = MatrixUtils.getTransformation();
        Matrix4d translation = transformation[0];

        this.position.x = (float) translation.m03;
        this.position.y = (float) translation.m13;
        this.position.z = (float) translation.m23;

        if (Minecraft.getMinecraft().gameSettings.showDebugInfo || GuiModelRenderer.isRendering())
        {
            GlStateManager.pushMatrix();

            RenderingUtils.glRevertRotationScale();

            this.renderPicture(x, y, z, partialTicks);

            GlStateManager.popMatrix();
        }

        if (GuiModelRenderer.isRendering())
        {
            this.addDummyEntityToWorld();

            this.renderedInEditor = true;
        }
        else
        {
            this.renderedInEditor = false;
        }

        if (!CustomMorph.isRenderingOnScreen())
        {
            this.updateDummyEntity();
        }

        this.lastRenderAge = (this.dummy != null) ? this.dummy.getAge() : 0;
        this.renderedOnScreen = false;
    }

    @SideOnly(Side.CLIENT)
    private void renderPicture(double x, double y, double z, float partialTicks)
    {
        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, Interpolation.LINEAR.interpolate(lastBrightnessX, 240, this.lightProperties.lightValue / 15F), lastBrightnessY);

        GlStateManager.enableRescaleNormal();

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        RenderingUtils.glFacingRotation(RenderingUtils.Facing.ROTATE_XYZ, this.position);

        GlStateManager.scale(0.5F, 0.5F, 0.5F);

        GlStateManager.disableDepth();
        GlStateManager.disableLighting();

        this.renderPictureTexture(1, partialTicks);

        GL11.glPopMatrix();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);

        GlStateManager.disableRescaleNormal();
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
    }

    @SideOnly(Side.CLIENT)
    private void renderPictureTexture(float scale, float partialTicks)
    {
        ResourceLocation image = new ResourceLocation(Blockbuster.MOD_ID, "textures/light_bulb" + this.lightProperties.lightValue + ".png");

        GifTexture.bindTexture(image, 0, partialTicks);

        boolean isCulling = GL11.glIsEnabled(GL11.GL_CULL_FACE);

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        if (ReflectionUtils.isOptifineShadowPass())
        {
            GlStateManager.disableCull();
        }
        else
        {
            GlStateManager.enableCull();
        }

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, VertexBuilder.getFormat(false, true, false, true));

        int perspective = Minecraft.getMinecraft().gameSettings.thirdPersonView;
        float width = scale * (perspective == 2 ? -1 : 1) * 0.5F;
        float height = scale * 0.5F;

        /* Frontface */
        buffer.pos(-width, height, 0.0F).tex(0, 0).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(-width, -height, 0.0F).tex(0, 1).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(width, -height, 0.0F).tex(1, 1).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(width, height, 0.0F).tex(1, 0).normal(0.0F, 0.0F, 1.0F).endVertex();

        buffer.pos(width,height, 0.0F).tex(1, 0).normal(0.0F, 0.0F, -1.0F).endVertex();
        buffer.pos(width, -height, 0.0F).tex(1, 1).normal(0.0F, 0.0F, -1.0F).endVertex();
        buffer.pos(-width, -height, 0.0F).tex(0, 1).normal(0.0F, 0.0F, -1.0F).endVertex();
        buffer.pos(-width, height, 0.0F).tex(0, 0).normal(0.0F, 0.0F, -1.0F).endVertex();

        tessellator.draw();

        if (isCulling)
        {
            GlStateManager.enableCull();
        }
        else
        {
            GlStateManager.disableCull();
        }

        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
    }

    @Override
    public void pause(AbstractMorph previous, int offset)
    {
        this.animation.pause(offset);

        if (previous instanceof LightMorph)
        {
            LightMorph lightMorph = (LightMorph) previous;

            this.animation.last = new LightProperties();
            this.animation.last.from(lightMorph);
        }
        else
        {
            this.animation.last = new LightProperties();
            this.animation.last.from(this);
        }
    }

    @Override
    public AbstractMorph genCurrentMorph(float partialTicks)
    {
        LightMorph morph = (LightMorph) this.copy();

        morph.lightProperties.from(this);
        this.animation.apply(morph.lightProperties, partialTicks);

        morph.animation.duration = this.animation.progress;

        return morph;
    }

    @SideOnly(Side.CLIENT)
    private void updateAnimation(float partialTicks)
    {
        if (this.animation.isInProgress())
        {
            this.lightProperties.from(this);
            this.animation.apply(this.lightProperties, partialTicks);
        }
        else
        {
            this.lightProperties.from(this);
        }
    }

    @Override
    public AbstractMorph create()
    {
        return new LightMorph();
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);

        if (from instanceof LightMorph)
        {
            LightMorph lightMorph = (LightMorph) from;

            this.light = lightMorph.light;
            this.animation.copy(lightMorph.animation);
            this.animation.reset();
        }
    }

    @Override
    public boolean canMerge(AbstractMorph morph)
    {
        if (morph instanceof LightMorph)
        {
            LightMorph lightMorph = (LightMorph) morph;

            this.mergeBasic(morph);

            /* animating in sequencer made the entity expire - this seems to fix it */
            if (this.dummy != null)
            {
                this.dummy.setLifetime(this.dummy.getAge() + 1);
            }

            if (!lightMorph.animation.ignored)
            {
                this.animation.merge(this, lightMorph);
                this.copy(lightMorph);
                this.animation.progress = 0;
            }
            else
            {
                this.animation.ignored = true;
            }

            return true;
        }

        return super.canMerge(morph);
    }

    @Override
    public boolean equals(Object object)
    {
        boolean result = super.equals(object);

        if (object instanceof LightMorph)
        {
            LightMorph morph = (LightMorph) object;

            result = result && Objects.equals(this.light, morph.light);
            result = result && Objects.equals(morph.animation, this.animation);

            return result;
        }

        return result;
    }

    @Override
    public void reset()
    {
        super.reset();

        this.animation.reset();
    }

    @Override
    public float getWidth(EntityLivingBase entityLivingBase)
    {
        return 0;
    }

    @Override
    public float getHeight(EntityLivingBase entityLivingBase)
    {
        return 0;
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("LightValue"))
        {
            this.light = tag.getInteger("LightValue");
        }
        if (tag.hasKey("Animation"))
        {
            this.animation.fromNBT(tag.getCompoundTag("Animation"));
        }
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (this.light != 15)
        {
            tag.setInteger("LightValue", this.light);
        }

        NBTTagCompound animation = this.animation.toNBT();

        if (!animation.hasNoTags())
        {
            tag.setTag("Animation", animation);
        }
    }

    public static class LightAnimation extends Animation
    {
        public LightProperties last;

        public void merge(LightMorph last, LightMorph next)
        {
            this.merge(next.animation);

            if (this.last == null)
            {
                this.last = new LightProperties();
            }

            this.last.from(last);
        }

        public void apply(LightProperties properties, float partialTicks)
        {
            if (this.last == null)
            {
                return;
            }

            float factor = this.getFactor(partialTicks);

            properties.lightValue = MathUtils.clamp(Math.round(this.interp.interpolate(this.last.lightValue, properties.lightValue, factor)), 0, 15);
        }

    }

    public static class LightProperties
    {
        private int lightValue;

        public void from(LightMorph morph)
        {
            this.lightValue = morph.light;
        }
    }
}
