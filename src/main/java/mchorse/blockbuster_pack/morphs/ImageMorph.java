package mchorse.blockbuster_pack.morphs;

import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.client.textures.GifTexture;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.MatrixUtils;
import mchorse.mclib.utils.ReflectionUtils;
import mchorse.mclib.utils.resources.RLUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.utils.Animation;
import mchorse.metamorph.api.morphs.utils.IAnimationProvider;
import mchorse.metamorph.api.morphs.utils.ISyncableMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4d;
import javax.vecmath.Vector4f;
import java.util.Objects;

/**
 * Image morph 
 * 
 * This bad boy is basically replacement for Imaginary
 */
public class ImageMorph extends AbstractMorph implements IAnimationProvider, ISyncableMorph
{
    public static final Matrix4f matrix = new Matrix4f();

    public static final Vector4d pos = new Vector4d();
    public static final Vector4d uv = new Vector4d();
    public static final Vector4d finalUv = new Vector4d();

    /**
     * Image morph's texture 
     */
    public ResourceLocation texture;

    /**
     * Whether an image morph gets shaded 
     */
    public boolean shaded = true;

    /**
     * Whether an image morph is affected by light map 
     */
    public boolean lighting = true;

    /**
     * Whether an image morph should be always look at the player
     */
    public boolean billboard = false;

    /**
     * Area to crop (x = left, z = right, y = top, w = bottom)
     */
    public Vector4f crop = new Vector4f();

    /**
     * Whether this image morph resizes cropped area
     */
    public boolean resizeCrop;

    /**
     * Color filter for the image morph
     */
    public int color = 0xffffffff;

    /**
     * UV horizontal shift
     */
    public float offsetX;

    /**
     * UV vertical shift
     */
    public float offsetY;

    /**
     * Rotation around Z axis
     */
    public float rotation;

    /**
     * TSR for image morph
     */
    public ModelTransform pose = new ModelTransform();

    /**
     * Whether this image morph should cut out background color
     */
    public boolean keying;

    /**
     * Whether Optifine's shadow should be disabled
     */
    public boolean shadow = true;

    public ImageAnimation animation = new ImageAnimation();
    public ImageProperties image = new ImageProperties();

    public ImageMorph()
    {
        super();

        this.name = "blockbuster.image";
    }

    @Override
    public void pause(AbstractMorph previous, int offset)
    {
        this.animation.pause(offset);

        if (previous instanceof ImageMorph)
        {
            ImageMorph image = (ImageMorph) previous;

            this.animation.last = new ImageMorph.ImageProperties();
            this.animation.last.from(image);
        }
        else
        {
            this.animation.last = new ImageMorph.ImageProperties();
            this.animation.last.from(this);
        }
    }

    @Override
    public boolean isPaused()
    {
        return this.animation.paused;
    }

    @Override
    public Animation getAnimation()
    {
        return this.animation;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getSubclassDisplayName()
    {
        return I18n.format("blockbuster.morph.image");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        if (this.texture == null)
        {
            return;
        }

        float partial = Minecraft.getMinecraft().getRenderPartialTicks();

        this.updateAnimation(partial);

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y - scale / 2, 0);
        GL11.glScalef(-1.5F, 1.5F, 1.5F);

        this.renderPicture(scale, player.ticksExisted, partial);

        GL11.glPopMatrix();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (!this.shadow && ReflectionUtils.isOptifineShadowPass())
        {
            return;
        }

        if (this.texture == null)
        {
            return;
        }

        this.updateAnimation(partialTicks);

        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;
        boolean defaultPose = this.image.pose.isDefault();

        GlStateManager.enableRescaleNormal();

        if (!this.shaded)
        {
            RenderHelper.disableStandardItemLighting();
        }

        if (!this.lighting)
        {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        }

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        if (!defaultPose)
        {
            this.image.pose.applyTranslate();
            this.image.pose.applyRotate();
        }

        if (this.billboard)
        {
            /* Get matrix */
            Matrix4f matrix4f = MatrixUtils.readModelView(matrix);
            Vector4f zero = new Vector4f(0, 0, 0, 1);

            matrix4f.transform(zero);
            matrix4f.setIdentity();
            matrix4f.setTranslation(new Vector3f(zero.x, zero.y, zero.z));
            matrix4f.transpose();

            MatrixUtils.loadModelView(matrix4f);

            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        }
        else
        {
            float entityPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

            GL11.glRotatef(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180.0F - entityPitch, 1.0F, 0.0F, 0.0F);
        }

        if (!defaultPose)
        {
            this.image.pose.applyScale();
        }

        this.renderPicture(1F, entity.ticksExisted, partialTicks);

        GL11.glPopMatrix();

        if (!this.shaded)
        {
            GlStateManager.enableLighting();
            GlStateManager.enableLight(0);
            GlStateManager.enableLight(1);
            GlStateManager.enableColorMaterial();
        }

        if (!this.lighting)
        {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        }

        GlStateManager.disableRescaleNormal();
    }

    @SideOnly(Side.CLIENT)
    private void updateAnimation(float partialTicks)
    {
        if (this.animation.isInProgress())
        {
            this.image.from(this);
            this.animation.apply(this.image, partialTicks);
        }
        else
        {
            this.image.from(this);
        }
    }

    private void renderPicture(float scale, int ticks, float partialTicks)
    {
        GifTexture.bindTexture(this.texture, ticks, partialTicks);

        float w = this.getWidth();
        float h = this.getHeight();
        float ow = w;
        float oh = h;

        /* x = u1, y = u2, z = v1, w = v2 */
        uv.x = this.image.crop.x / (double) w;
        uv.y = 1.0F - this.image.crop.z / (double) w;
        uv.z = this.image.crop.y / (double) h;
        uv.w = 1.0F - this.image.crop.w / (double) h;

        finalUv.set(uv);

        if (this.resizeCrop)
        {
            finalUv.set(0F, 1F, 0F, 1F);

            w = w - this.image.crop.x - this.image.crop.z;
            h = h - this.image.crop.y - this.image.crop.w;
        }

        double ratioX = w > h ? h / (double) w : 1D;
        double ratioY = h > w ? w / (double) h : 1D;

        pos.set(-(finalUv.x - 0.5) * ratioY, -(finalUv.y - 0.5) * ratioY, (finalUv.z - 0.5) * ratioX, (finalUv.w - 0.5) * ratioX);
        pos.scale(scale);

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        if (this.keying)
        {
            GlStateManager.glBlendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
            GlStateManager.blendFunc(GL11.GL_ZERO, GL11.GL_ZERO);
        }
        else
        {
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        boolean textureMatrix = this.image.x != 0 || this.image.y != 0 || this.image.rotation != 0;

        if (textureMatrix)
        {
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.5F, 0.5F, 0);
            GlStateManager.translate(this.image.x / ow, this.image.y / oh, 0);
            GlStateManager.rotate(this.image.rotation, 0, 0, 1);
            GlStateManager.translate(-0.5F, -0.5F, 0);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

        Color color = this.image.color;

        buffer.pos(pos.x, pos.z, 0).tex(uv.x, uv.z).color(color.r, color.g, color.b, color.a).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(pos.y, pos.z, 0).tex(uv.y, uv.z).color(color.r, color.g, color.b, color.a).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(pos.y, pos.w, 0).tex(uv.y, uv.w).color(color.r, color.g, color.b, color.a).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(pos.x, pos.w, 0).tex(uv.x, uv.w).color(color.r, color.g, color.b, color.a).normal(0.0F, 0.0F, 1.0F).endVertex();

        tessellator.draw();

        if (textureMatrix)
        {
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }

        if (this.keying)
        {
            GlStateManager.glBlendEquation(GL14.GL_FUNC_ADD);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }

        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.enableCull();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
    }

    @Override
    public void update(EntityLivingBase target)
    {
        super.update(target);

        this.animation.update();
    }

    public int getWidth()
    {
        return GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
    }

    public int getHeight()
    {
        return GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
    }

    @Override
    public AbstractMorph create()
    {
        return new ImageMorph();
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);

        if (from instanceof ImageMorph)
        {
            ImageMorph morph = (ImageMorph) from;

            this.texture = RLUtils.clone(morph.texture);
            this.shaded = morph.shaded;
            this.lighting = morph.lighting;
            this.billboard = morph.billboard;
            this.crop.set(morph.crop);
            this.resizeCrop = morph.resizeCrop;
            this.color = morph.color;
            this.offsetX = morph.offsetX;
            this.offsetY = morph.offsetY;
            this.rotation = morph.rotation;
            this.pose.copy(morph.pose);
            this.keying = morph.keying;
            this.shadow = morph.shadow;
            this.animation.copy(morph.animation);
            this.animation.reset();
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof ImageMorph)
        {
            ImageMorph image = (ImageMorph) obj;

            result = result && Objects.equals(image.texture, this.texture);
            result = result && image.shaded == this.shaded;
            result = result && image.lighting == this.lighting;
            result = result && image.billboard == this.billboard;
            result = result && image.crop.equals(this.crop);
            result = result && image.resizeCrop == this.resizeCrop;
            result = result && image.color == this.color;
            result = result && image.offsetX == this.offsetX;
            result = result && image.offsetY == this.offsetY;
            result = result && image.rotation == this.rotation;
            result = result && Objects.equals(image.pose, this.pose);
            result = result && image.keying == this.keying;
            result = result && image.shadow == this.shadow;
            result = result && Objects.equals(image.animation, this.animation);
        }

        return result;
    }

    @Override
    public boolean canMerge(AbstractMorph morph)
    {
        if (morph instanceof ImageMorph)
        {
            ImageMorph image = (ImageMorph) morph;

            this.mergeBasic(morph);

            if (!image.animation.ignored)
            {
                this.animation.merge(this, image);
                this.copy(image);
                this.animation.progress = 0;
            }

            return true;
        }

        return super.canMerge(morph);
    }

    @Override
    public float getWidth(EntityLivingBase target)
    {
        return 0;
    }

    @Override
    public float getHeight(EntityLivingBase target)
    {
        return 0;
    }

    @Override
    public void reset()
    {
        super.reset();

        this.animation.reset();
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (this.texture != null) tag.setTag("Texture", RLUtils.writeNbt(this.texture));
        if (this.shaded == false) tag.setBoolean("Shaded", this.shaded);
        if (this.lighting == false) tag.setBoolean("Lighting", this.lighting);
        if (this.billboard == true) tag.setBoolean("Billboard", this.billboard);
        if (this.crop.x != 0) tag.setInteger("Left", (int) this.crop.x);
        if (this.crop.z != 0) tag.setInteger("Right", (int) this.crop.z);
        if (this.crop.y != 0) tag.setInteger("Top", (int) this.crop.y);
        if (this.crop.w != 0) tag.setInteger("Bottom", (int) this.crop.w);
        if (this.resizeCrop) tag.setBoolean("ResizeCrop", this.resizeCrop);
        if (this.color != 0xffffffff) tag.setInteger("Color", this.color);
        if (this.offsetX != 0) tag.setFloat("OffsetX", this.offsetX);
        if (this.offsetY != 0) tag.setFloat("OffsetY", this.offsetY);
        if (this.rotation != 0) tag.setFloat("Rotation", this.rotation);
        if (!this.pose.isDefault()) tag.setTag("Pose", this.pose.toNBT());
        if (this.keying) tag.setBoolean("Keying", this.keying);
        if (!this.shadow) tag.setBoolean("Shadow", this.shadow);

        NBTTagCompound animation = this.animation.toNBT();

        if (!animation.hasNoTags())
        {
            tag.setTag("Animation", animation);
        }
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("Texture")) this.texture = RLUtils.create(tag.getTag("Texture"));
        if (tag.hasKey("Shaded")) this.shaded = tag.getBoolean("Shaded");
        if (tag.hasKey("Lighting")) this.lighting = tag.getBoolean("Lighting");
        if (tag.hasKey("Billboard")) this.billboard = tag.getBoolean("Billboard");
        if (tag.hasKey("Left")) this.crop.x = tag.getInteger("Left");
        if (tag.hasKey("Right")) this.crop.z = tag.getInteger("Right");
        if (tag.hasKey("Top")) this.crop.y = tag.getInteger("Top");
        if (tag.hasKey("Bottom")) this.crop.w = tag.getInteger("Bottom");
        if (tag.hasKey("ResizeCrop")) this.resizeCrop = tag.getBoolean("ResizeCrop");
        if (tag.hasKey("Color")) this.color = tag.getInteger("Color");
        if (tag.hasKey("OffsetX")) this.offsetX = tag.getFloat("OffsetX");
        if (tag.hasKey("OffsetY")) this.offsetY = tag.getFloat("OffsetY");
        if (tag.hasKey("Rotation")) this.rotation = tag.getFloat("Rotation");
        if (tag.hasKey("Animation")) this.animation.fromNBT(tag.getCompoundTag("Animation"));
        if (tag.hasKey("Pose")) this.pose.fromNBT(tag.getCompoundTag("Pose"));
        if (tag.hasKey("Keying")) this.keying = tag.getBoolean("Keying");
        if (tag.hasKey("Shadow")) this.shadow = tag.getBoolean("Shadow");

        if (tag.hasKey("Scale"))
        {
            float scale = tag.getFloat("Scale");

            this.pose.scale[0] = scale;
            this.pose.scale[1] = scale;
            this.pose.scale[2] = scale;
        }
    }

    public static class ImageAnimation extends Animation
    {
        public ImageProperties last = new ImageProperties();

        public void merge(ImageMorph last, ImageMorph next)
        {
            this.merge(next.animation);
            this.last.from(last);
        }

        public void apply(ImageProperties properties, float partialTicks)
        {
            float factor = this.getFactor(partialTicks);

            properties.color.r = this.interp.interpolate(this.last.color.r, properties.color.r, factor);
            properties.color.g = this.interp.interpolate(this.last.color.g, properties.color.g, factor);
            properties.color.b = this.interp.interpolate(this.last.color.b, properties.color.b, factor);
            properties.color.a = this.interp.interpolate(this.last.color.a, properties.color.a, factor);
            properties.crop.x = (int) this.interp.interpolate(this.last.crop.x, properties.crop.x, factor);
            properties.crop.y = (int) this.interp.interpolate(this.last.crop.y, properties.crop.y, factor);
            properties.crop.z = (int) this.interp.interpolate(this.last.crop.z, properties.crop.z, factor);
            properties.crop.w = (int) this.interp.interpolate(this.last.crop.w, properties.crop.w, factor);
            properties.pose.interpolate(this.last.pose, properties.pose, factor, this.interp);
            properties.x = this.interp.interpolate(this.last.x, properties.x, factor);
            properties.y = this.interp.interpolate(this.last.y, properties.y, factor);
            properties.rotation = this.interp.interpolate(this.last.rotation, properties.rotation, factor);
        }
    }

    public static class ImageProperties
    {
        public Color color = new Color();
        public Vector4f crop = new Vector4f();
        public ModelTransform pose = new ModelTransform();
        public float x;
        public float y;
        public float rotation;

        public void from(ImageMorph morph)
        {
            this.color.set(morph.color, true);
            this.crop.set(morph.crop);
            this.pose.copy(morph.pose);
            this.x = morph.offsetX;
            this.y = morph.offsetY;
            this.rotation = morph.rotation;
        }
    }
}