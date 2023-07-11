package mchorse.blockbuster.client.particles;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public enum  BedrockMaterial
{
    OPAQUE("particles_opaque"), ALPHA("particles_alpha"), BLEND("particles_blend"), ADDITIVE("particles_add");

    public final String id;

    public static BedrockMaterial fromString(String material)
    {
        for (BedrockMaterial mat : values())
        {
            if (mat.id.equals(material))
            {
                return mat;
            }
        }

        return OPAQUE;
    }

    private BedrockMaterial(String id)
    {
        this.id = id;
    }

    public void beginGL()
    {
        switch (this)
        {
            case OPAQUE:
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
                GlStateManager.disableBlend();
                GlStateManager.enableAlpha();
                break;
            case ALPHA:
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
                GlStateManager.disableBlend();
                GlStateManager.enableAlpha();
                break;
            case BLEND:
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.enableAlpha();
                break;
            case ADDITIVE:
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.enableAlpha();
                break;
        }
    }

    public void endGL()
    {
        switch (this)
        {
            case OPAQUE:
            case ALPHA:
            case BLEND:
            case ADDITIVE:
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.disableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
                break;
        }
    }

}