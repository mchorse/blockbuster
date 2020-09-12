package mchorse.blockbuster.audio;

import mchorse.blockbuster.ClientProxy;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.nio.charset.MalformedInputException;

@SideOnly(Side.CLIENT)
public class AudioRenderer
{
	public static void renderAll(int x, int y, int w, int h, int sw, int sh, boolean paused)
	{
		for (AudioFile file : ClientProxy.audio.files.values())
		{
			if (file.player.isPlaying() || (paused && file.player.isPaused()))
			{
				AudioRenderer.renderWaveform(file, x, y, w, h, sw, sh);

				y -= h + 5;
			}
		}
	}

	public static void renderWaveform(AudioFile file, int x, int y, int w, int h, int sw, int sh)
	{
		final float brightness = 0.45F;

		/* Draw background */
		GuiDraw.drawVerticalGradientRect(x + 2, y + 2, x + w - 2, y + h, 0x00000000, 0x88000000);
		Gui.drawRect(x + 1, y, x + 2, y + h, 0xaaffffff);
		Gui.drawRect(x + w - 2, y, x + w - 1, y + h, 0xaaffffff);
		Gui.drawRect(x, y + h - 1, x + w, y + h, 0xffffffff);

		GuiDraw.scissor(x + 2, y + 2, w - 4, h - 4, sw, sh);

		if (!file.waveform.isCreated())
		{
			file.waveform.render();
		}

		int offset = (int) (file.player.getPlaybackPosition() * 20 - 0.25F);
		int half = (w - 2) / 2;

		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.bindTexture(file.waveform.getTexture());

		if (offset < half)
		{
			if (offset != 0)
			{
				GuiDraw.drawBillboard(x + 1 + half, y + 1, offset, 0, w, h, file.waveform.getWidth(), 20);

				GlStateManager.color(brightness, brightness, brightness);
				GuiDraw.drawBillboard(x + 1 + half - offset, y + 1, 0, 0, offset, h, file.waveform.getWidth(), 20);
				GlStateManager.color(1, 1, 1);
			}
			else
			{
				GuiDraw.drawBillboard(x + 1 + (half - offset), y + 1, 0, 0, w, h, file.waveform.getWidth(), 20);
			}
		}
		else if (offset > file.waveform.getWidth() - half)
		{
			int diff = offset - (file.waveform.getWidth() - half);

			GuiDraw.drawBillboard(x + 1, y + 1, offset - half, 0, w - diff - 3, h, file.waveform.getWidth(), 20);

			GlStateManager.color(brightness, brightness, brightness);
			GuiDraw.drawBillboard(x + 1, y + 1, offset - half, 0,  half, h, file.waveform.getWidth(), 20);
			GlStateManager.color(1, 1, 1);
		}
		else
		{
			GuiDraw.drawBillboard(x + 1 + half, y + 1, offset, 0, w, h, file.waveform.getWidth(), 20);

			GlStateManager.color(brightness, brightness, brightness);
			GuiDraw.drawBillboard(x + 1, y + 1, offset - half, 0, w - half - 1, h, file.waveform.getWidth(), 20);
			GlStateManager.color(1, 1, 1);
		}

		GuiDraw.unscissor(sw, sh);

		half = w / 2;

		Gui.drawRect(x + half, y + 1, x + half + 1, y + h - 1, 0xff57f52a);
		GuiDraw.drawTextBackground(Minecraft.getMinecraft().fontRenderer, file.name, x + 8, y + h / 2 - 4, 0xffffff, 0x88000000);
	}
}