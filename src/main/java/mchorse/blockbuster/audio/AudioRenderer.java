package mchorse.blockbuster.audio;

import mchorse.blockbuster.ClientProxy;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		Gui.drawRect(x, y, x + w, y + h, 0x88000000);

		GuiDraw.scissor(x + 2, y + 2, w - 4, h - 4, sw, sh);

		if (!file.waveform.isCreated())
		{
			file.waveform.render();
		}

		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.bindTexture(file.waveform.getTexture());
		GuiDraw.drawBillboard(x + 2, y + 2, (int) (file.player.getPlaybackPosition() * 20 - 0.25F), 0, w, h, file.waveform.getWidth(), 20);

		GuiDraw.unscissor(sw, sh);
	}
}