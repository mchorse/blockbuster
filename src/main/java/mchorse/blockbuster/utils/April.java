package mchorse.blockbuster.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class April
{
	public static ResourceLocation BEACH = new ResourceLocation("https://www.ecopetit.cat/wpic/mpic/112-1122097_anime-girl-background.jpg");

	public static int aprilColor(String string)
	{
		long time = System.currentTimeMillis() + string.hashCode();
		float h = (float) (time / 1000D % 1D);

		return MathHelper.hsvToRGB(h, 1, 0.85F);
	}

	public static void drawAnimuBackground(int x, int y, int w, int h, float a)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(BEACH);

		int mag = GL11.GL_LINEAR;

		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mag);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mag);

		GuiScreen screen = Minecraft.getMinecraft().currentScreen;

		int xx = (int) (x / (float) screen.width * 1920);
		int yy = (int) (y / (float) screen.height * 1080);
		int ww = (int) (w / (float) screen.width * 1920);
		int hh = (int) (h / (float) screen.height * 1080);

		float luma = 0.25F;
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.color(luma, luma, luma, 0.85F);
		drawBillboard(x, y, xx, yy, w, h, ww, hh, 1920, 1080);
	}

	public static void drawBillboard(int x, int y, int u, int v, int w, int h, int tw, int th, int textureW, int textureH)
	{
		drawBillboard(x, y, u, v, w, h, textureW, textureH, tw, th, 0);
	}

	/**
	 * Draw a textured quad with given UV, dimensions and custom texture size
	 */
	public static void drawBillboard(int x, int y, int u, int v, int w, int h, int tw, int th, int textureW, int textureH, float z)
	{
		float fw = 1F / textureW;
		float fh = 1F / textureH;

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x, y + h, z).tex(u * fw, (v + th) * fh).endVertex();
		buffer.pos(x + w, y + h, z).tex((u + tw) * fw, (v + th) * fh).endVertex();
		buffer.pos(x + w, y, z).tex((u + tw) * fw, v * fh).endVertex();
		buffer.pos(x, y, z).tex(u * fw, v * fh).endVertex();

		tessellator.draw();
	}
}