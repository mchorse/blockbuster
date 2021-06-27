package mchorse.blockbuster.audio;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.wav.Waveform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;

@SideOnly(Side.CLIENT)
public class AudioRenderer
{
    public static void renderAll(int x, int y, int w, int h, int sw, int sh)
    {
        if (!Blockbuster.audioWaveformVisible.get())
        {
            return;
        }

        /* Make the anchor at the bottom */
        y -= h;

        for (AudioFile file : ClientProxy.audio.files.values())
        {
            if (!file.isEmpty() && !file.player.isStopped())
            {
                AudioRenderer.renderWaveform(file, x, y, w, h, sw, sh);

                y -= h + 5;
            }
        }
    }

    public static void renderWaveform(AudioFile file, int x, int y, int w, int h, int sw, int sh)
    {
        if (file == null || file.isEmpty())
        {
            return;
        }

        final float brightness = 0.45F;
        int half = w / 2;

        /* Draw background */
        GuiDraw.drawVerticalGradientRect(x + 2, y + 2, x + w - 2, y + h, 0, ColorUtils.HALF_BLACK);
        Gui.drawRect(x + 1, y, x + 2, y + h, 0xaaffffff);
        Gui.drawRect(x + w - 2, y, x + w - 1, y + h, 0xaaffffff);
        Gui.drawRect(x, y + h - 1, x + w, y + h, 0xffffffff);

        GuiDraw.scissor(x + 2, y + 2, w - 4, h - 4, sw, sh);

        Waveform wave = file.waveform;

        if (!wave.isCreated())
        {
            wave.render();
        }

        float playback = file.player.getPlaybackPosition();
        int offset = (int) (playback * wave.getPixelsPerSecond());
        int waveW = file.waveform.getWidth();

        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        /* Draw the waveform */
        int runningOffset = waveW - offset;

        if (runningOffset > 0)
        {
            file.waveform.draw(x + half, y, offset, 0, Math.min(runningOffset, half), h, h);
        }

        /* Draw the passed waveform */
        if (offset > 0)
        {
            int xx = offset > half ? x : x + half - offset;
            int oo = offset > half ? offset - half : 0;
            int ww = offset > half ? half : offset;

            GlStateManager.color(brightness, brightness, brightness);
            file.waveform.draw(xx, y, oo, 0, ww, h, h);
            GlStateManager.color(1, 1, 1);
        }

        GuiDraw.unscissor(sw, sh);

        Gui.drawRect(x + half, y + 1, x + half + 1, y + h - 1, 0xff57f52a);

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        if (Blockbuster.audioWaveformFilename.get())
        {
            GuiDraw.drawTextBackground(fontRenderer, file.name, x + 8, y + h / 2 - 4, 0xffffff, 0x99000000);
        }

        if (Blockbuster.audioWaveformTime.get())
        {
            int tick = (int) Math.floor(playback * 20);
            int seconds = tick / 20;
            int milliseconds = (int) (tick % 20 == 0 ? 0 : tick % 20 * 5D);

            String tickLabel = tick + "t (" + seconds + "." + StringUtils.leftPad(String.valueOf(milliseconds), 2, "0") + "s)";

            GuiDraw.drawTextBackground(fontRenderer, tickLabel, x + w - 8 - fontRenderer.getStringWidth(tickLabel), y + h / 2 - 4, 0xffffff, 0x99000000);
        }
    }
}