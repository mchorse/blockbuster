package mchorse.blockbuster.client.textures;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Mipmap texture class
 * 
 * This class is responsible for loading or rather replacing a regular 
 * texture with a mipmapped version of a texture. 
 */
@SideOnly(Side.CLIENT)
public class MipmapTexture extends SimpleTexture
{
    /**
     * Create a byte buffer from buffered image 
     */
    public static ByteBuffer bytesFromBuffer(BufferedImage image)
    {
        int w = image.getWidth();
        int h = image.getHeight();

        ByteBuffer buffer = GLAllocation.createDirectByteBuffer(w * h * 4);
        int[] pixels = new int[w * h];

        image.getRGB(0, 0, w, h, pixels, 0, w);

        for (int y = 0; y < h; y++)
        {
            for (int x = 0; x < w; x++)
            {
                int pixel = pixels[y * w + x];

                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        buffer.flip();

        return buffer;
    }

    public MipmapTexture(ResourceLocation textureResourceLocation)
    {
        super(textureResourceLocation);
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
        IResource resource = null;

        try
        {
            resource = resourceManager.getResource(this.textureLocation);
            BufferedImage image = TextureUtil.readBufferedImage(resource.getInputStream());

            int id = this.getGlTextureId();
            int w = image.getWidth();
            int h = image.getHeight();

            GlStateManager.bindTexture(id);
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MIN_LOD, 0);
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, 3);
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 3);
            GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.0F);
            GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
            GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
            GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bytesFromBuffer(image));
        }
        finally
        {
            IOUtils.closeQuietly(resource);
        }
    }
}