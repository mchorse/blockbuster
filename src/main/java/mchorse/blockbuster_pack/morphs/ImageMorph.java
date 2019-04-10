package mchorse.blockbuster_pack.morphs;

import java.util.Objects;

import org.lwjgl.opengl.GL11;

import mchorse.mclib.utils.resources.RLUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Image morph 
 * 
 * This bad boy is basically replacement for 
 */
public class ImageMorph extends AbstractMorph
{
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

    public ImageMorph()
    {
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
            EntityPlayer player = Minecraft.getMinecraft().player;

            entityYaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
            float entityPitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;

            GL11.glRotatef(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180.0F - entityPitch, 1.0F, 0.0F, 0.0F);

            if (Minecraft.getMinecraft().gameSettings.thirdPersonView != 2)
            {
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }
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
        Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);

        int w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        double x1 = 0;
        double x2 = 0;
        double y1 = 0;
        double y2 = 0;

        if (w > h)
        {
            x1 = -0.5;
            x2 = 0.5;
            y1 = -(float) h / w * 0.5;
            y2 = -y1;
        }
        else
        {
            x1 = -(float) w / h * 0.5;
            x2 = -x1;
            y1 = -0.5;
            y2 = 0.5;
        }

        double u1 = 1.0F;
        double u2 = 0.0F;
        double v1 = 1.0F;
        double v2 = 0.0F;

        if (flipX)
        {
            u1 = 0.0F;
            u2 = 1.0F;
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
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

        vertexbuffer.pos(x1, y1, 0).tex(u2, v2).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexbuffer.pos(x2, y1, 0).tex(u1, v2).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexbuffer.pos(x2, y2, 0).tex(u1, v1).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexbuffer.pos(x1, y2, 0).tex(u2, v1).normal(0.0F, 0.0F, 1.0F).endVertex();

        tessellator.draw();

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
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
    }
}