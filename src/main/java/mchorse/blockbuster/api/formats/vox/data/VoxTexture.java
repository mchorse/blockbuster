package mchorse.blockbuster.api.formats.vox.data;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

public class VoxTexture
{
	/**
	 * OpenGL texture ID
	 */
	private int texture = -1;

	private int[] palette;

	public VoxTexture(int[] palette)
	{
		this.palette = palette;
	}

	/**
	 * Get OpenGL texture of this document's palette
	 */
	public int getTexture()
	{
		int count = this.palette.length;

		if (count > 0 && this.texture == -1)
		{
			ByteBuffer buffer = GLAllocation.createDirectByteBuffer(count * 4);
			this.texture = GL11.glGenTextures();

			for (int color : this.palette)
			{
				int r = color >> 16 & 255;
				int g = color >> 8 & 255;
				int b = color & 255;
				int a = color >> 24 & 255;

				buffer.put((byte) r);
				buffer.put((byte) g);
				buffer.put((byte) b);
				buffer.put((byte) a);
			}

			buffer.flip();

			/* For some reason, if there is no glTexParameter calls
			 * the texture becomes pure white */
			GlStateManager.bindTexture(texture);
			GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, count, 1, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		}

		return this.texture;
	}

	public void deleteTexture()
	{
		if (this.texture != -1)
		{
			GlStateManager.deleteTexture(this.texture);
			this.texture = -1;
		}
	}
}