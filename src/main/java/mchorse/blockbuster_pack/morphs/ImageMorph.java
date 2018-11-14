package mchorse.blockbuster_pack.morphs;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.utils.TextureLocation;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
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
     * Whether image morph gets shaded 
     */
    public boolean shaded;

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
        GL11.glTranslatef(x, y - scale / 2, 100);

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

        if (!this.shaded)
        {
            RenderHelper.disableStandardItemLighting();
        }

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F - entity.rotationPitch, 1.0F, 0.0F, 0.0F);

        this.renderPicture(this.scale, true);

        GL11.glPopMatrix();

        if (!this.shaded)
        {
            GlStateManager.enableLighting();
            GlStateManager.enableLight(0);
            GlStateManager.enableLight(1);
            GlStateManager.enableColorMaterial();
        }
    }

    private void renderPicture(float scale, boolean flipX)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

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
        BufferBuilder vertexbuffer = tessellator.getBuffer();
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
        morph.texture = this.texture;
        morph.scale = this.scale;
        morph.shaded = this.shaded;

        return morph;
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof ImageMorph)
        {
            ImageMorph image = (ImageMorph) obj;

            result = result && image.texture.equals(this.texture);
            result = result && image.scale == this.scale;
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
        if (this.texture != null) tag.setString("Texture", this.texture.toString());
        if (this.shaded != false) tag.setBoolean("Shaded", this.shaded);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("Scale")) this.scale = tag.getFloat("Scale");
        if (tag.hasKey("Texture", 8)) this.texture = new TextureLocation(tag.getString("Texture"));
        if (tag.hasKey("Shaded")) this.shaded = tag.getBoolean("Shaded");
    }
}