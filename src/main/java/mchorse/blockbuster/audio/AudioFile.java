package mchorse.blockbuster.audio;

import mchorse.mclib.utils.wav.WavePlayer;
import mchorse.mclib.utils.wav.Waveform;
import org.lwjgl.openal.AL10;

import java.io.File;

public class AudioFile
{
    public String name;
    public File file;
    public WavePlayer player;
    public Waveform waveform;
    public long update;

    private boolean wasPaused;

    public AudioFile(String name, File file, WavePlayer player, Waveform waveform, long update)
    {
        this.name = name;
        this.file = file;
        this.player = player;
        this.waveform = waveform;
        this.update = update;
    }

    public boolean canBeUpdated()
    {
        return this.update < this.file.lastModified();
    }

    public boolean isEmpty()
    {
        return this.player == null || this.waveform == null;
    }

    public void delete()
    {
        if (this.player != null)
        {
            this.player.delete();
            this.player = null;
        }

        if (this.waveform != null)
        {
            this.waveform.delete();
            this.waveform = null;
        }
    }

    public void pause(boolean pause)
    {
        if (this.player == null) return;

        int state = this.player.getSourceState();

        if (!pause && this.wasPaused)
        {
            this.wasPaused = false;

            return;
        }

        this.wasPaused = pause && state == AL10.AL_PAUSED;

        if (pause && state == AL10.AL_PLAYING)
        {
            this.player.pause();
        }
        else if (!pause && state == AL10.AL_PAUSED)
        {
            this.player.play();
        }
    }
}