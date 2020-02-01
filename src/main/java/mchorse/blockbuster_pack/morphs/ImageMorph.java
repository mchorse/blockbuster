package mchorse.blockbuster_pack.morphs;

import mchorse.blockbuster.client.textures.GifTexture;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.utils.resources.RLUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.nio.FloatBuffer;
import java.util.Objects;

/**
 * Image morph 
 * 
 * This bad boy is basically replacement for 
 */
public class ImageMorph extends AbstractMorph
{
    /**
     * Model view matrix buffer
     */
    public static final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);

    public static final float[] floats = new float[16];

    /**
     * Image morph's texture 
     */
    public ResourceLocation texture;

    /**
     * Image morph's scale
     */
    public float scale = 1;

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
     * Area to crop (x = left, w = right, y = top, h = bottom)
     */
    public Area cropping = new Area();

    public ImageMorph()
    {
        super();

        this.name = "blockbuster.image";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        if (this.texture == null)
        {
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y - scale / 2, 0);
        GL11.glScalef(1.5F, 1.5F, 1.5F);

        this.renderPicture(scale, false);

        GL11.glPopMatrix();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (this.texture == null)
        {
            return;
        }

        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;

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

        if (this.billboard)
        {
            /* Get matrix */
            buffer.clear();
            GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buffer);
            buffer.get(floats);

            Matrix4f matrix4f = new Matrix4f();
            matrix4f.setIdentity();
            matrix4f.set(floats);
            matrix4f.transpose();

            Vector4f zero = new Vector4f(0, 0, 0, 1);

            matrix4f.transform(zero);
            matrix4f.setIdentity();
            matrix4f.setTranslation(new Vector3f(zero.x, zero.y, zero.z));
            matrix4f.transpose();

            floats[0] = matrix4f.m00;
            floats[1] = matrix4f.m01;
            floats[2] = matrix4f.m02;
            floats[3] = matrix4f.m03;
            floats[4] = matrix4f.m10;
            floats[5] = matrix4f.m11;
            floats[6] = matrix4f.m12;
            floats[7] = matrix4f.m13;
            floats[8] = matrix4f.m20;
            floats[9] = matrix4f.m21;
            floats[10] = matrix4f.m22;
            floats[11] = matrix4f.m23;
            floats[12] = matrix4f.m30;
            floats[13] = matrix4f.m31;
            floats[14] = matrix4f.m32;
            floats[15] = matrix4f.m33;

            buffer.clear();
            buffer.put(floats);
            buffer.rewind();
            GL11.glLoadMatrix(buffer);

            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        }
        else
        {
            float entityPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

            GL11.glRotatef(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180.0F - entityPitch, 1.0F, 0.0F, 0.0F);
        }

        this.renderPicture(this.scale, true);

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

    private void renderPicture(float scale, boolean flipX)
    {
        GifTexture.bindTexture(this.texture);

        int w = this.getWidth();
        int h = this.getHeight();

        double x1 = 0;
        double x2 = 0;
        double y1 = 0;
        double y2 = 0;

        double u1 = 1.0F - (flipX ? this.cropping.x : this.cropping.w) / (double) w;
        double u2 = (flipX ? this.cropping.w : this.cropping.x) / (double) w;
        double v1 = 1.0F - this.cropping.h / (double) h;
        double v2 = this.cropping.y / (double) h;

        if (w > h)
        {
            double ratio = h / (double) w;

            x1 = (u2 - 0.5);
            x2 = (u1 - 0.5);
            y1 = (v2 - 0.5) * ratio;
            y2 = (v1 - 0.5) * ratio;
        }
        else
        {
            double ratio = w / (double) h;

            x1 = (u2 - 0.5) * ratio;
            x2 = (u1 - 0.5) * ratio;
            y1 = (v2 - 0.5);
            y2 = (v1 - 0.5);
        }

        if (flipX)
        {
            u1 = 1 - u1;
            u2 = 1 - u2;
        }

        x1 *= scale;
        x2 *= scale;
        y1 *= scale;
        y2 *= scale;

        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

        vertexbuffer.pos(x1, y1, 0).tex(u2, v2).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexbuffer.pos(x2, y1, 0).tex(u1, v2).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexbuffer.pos(x2, y2, 0).tex(u1, v1).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexbuffer.pos(x1, y2, 0).tex(u2, v1).normal(0.0F, 0.0F, 1.0F).endVertex();

        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.enableCull();
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
    public AbstractMorph clone(boolean isRemote)
    {
        ImageMorph morph = new ImageMorph();

        morph.name = this.name;
        morph.settings = this.settings;
        morph.texture = RLUtils.clone(this.texture);
        morph.scale = this.scale;
        morph.shaded = this.shaded;
        morph.lighting = this.lighting;
        morph.billboard = this.billboard;
        morph.cropping.copy(this.cropping);

        return morph;
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof ImageMorph)
        {
            ImageMorph image = (ImageMorph) obj;

            result = result && Objects.equals(image.texture, this.texture);
            result = result && image.scale == this.scale;
            result = result && image.shaded == this.shaded;
            result = result && image.lighting == this.lighting;
            result = result && image.billboard == this.billboard;
            result = result && image.cropping.equals(this.cropping);
        }

        return result;
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
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (this.scale != 1) tag.setFloat("Scale", this.scale);
        if (this.texture != null) tag.setTag("Texture", RLUtils.writeNbt(this.texture));
        if (this.shaded == false) tag.setBoolean("Shaded", this.shaded);
        if (this.lighting == false) tag.setBoolean("Lighting", this.lighting);
        if (this.billboard == true) tag.setBoolean("Billboard", this.billboard);
        if (this.cropping.x != 0) tag.setInteger("Left", this.cropping.x);
        if (this.cropping.w != 0) tag.setInteger("Right", this.cropping.w);
        if (this.cropping.y != 0) tag.setInteger("Top", this.cropping.y);
        if (this.cropping.h != 0) tag.setInteger("Bottom", this.cropping.h);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("Scale")) this.scale = tag.getFloat("Scale");
        if (tag.hasKey("Texture")) this.texture = RLUtils.create(tag.getTag("Texture"));
        if (tag.hasKey("Shaded")) this.shaded = tag.getBoolean("Shaded");
        if (tag.hasKey("Lighting")) this.lighting = tag.getBoolean("Lighting");
        if (tag.hasKey("Billboard")) this.billboard = tag.getBoolean("Billboard");
        if (tag.hasKey("Left")) this.cropping.x = tag.getInteger("Left");
        if (tag.hasKey("Right")) this.cropping.w = tag.getInteger("Right");
        if (tag.hasKey("Top")) this.cropping.y = tag.getInteger("Top");
        if (tag.hasKey("Bottom")) this.cropping.h = tag.getInteger("Bottom");
    }
}