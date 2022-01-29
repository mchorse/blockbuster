package mchorse.blockbuster.audio;

import mchorse.blockbuster.Blockbuster;
import mchorse.mclib.utils.LatencyTimer;
import mchorse.mclib.utils.wav.Wave;
import mchorse.mclib.utils.wav.WavePlayer;
import mchorse.mclib.utils.wav.WaveReader;
import mchorse.mclib.utils.wav.Waveform;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioLibrary
{
    public File folder;
    public Map<String, AudioFile> files = new HashMap<String, AudioFile>();

    public AudioLibrary(File folder)
    {
        this.folder = folder;
        this.folder.mkdirs();
    }

    public List<File> getFiles()
    {
        File[] files = this.folder.listFiles();

        if (files == null)
        {
            return Collections.emptyList();
        }

        List<File> list = new ArrayList<File>();

        for (File file : files)
        {
            if (!file.getName().endsWith(".wav"))
            {
                continue;
            }

            list.add(file);
        }

        return list;
    }

    public List<String> getFileNames()
    {
        List<String> list = new ArrayList<String>();

        for (File file : this.getFiles())
        {
            String name = file.getName();

            list.add(name.substring(0, name.length() - 4));
        }

        return list;
    }

    private AudioFile load(String name, File file)
    {
        if (!file.isFile())
        {
            return null;
        }

        AudioFile audio;

        try
        {
            Wave wave = new WaveReader().read(new FileInputStream(file));

            if (wave.getBytesPerSample() > 2)
            {
                wave = wave.convertTo16();
            }

            WavePlayer player = new WavePlayer().initialize(wave);
            Waveform waveform = new Waveform();

            waveform.populate(wave, Blockbuster.audioWaveformDensity.get(), 40);

            audio = new AudioFile(name + ".wav", file, player, waveform, file.lastModified());
        }
        catch (Exception e)
        {
            e.printStackTrace();

            /* Empty */
            audio = new AudioFile(name + ".wav", file, null, null, file.lastModified());
        }

        this.files.put(name, audio);

        return audio;
    }

    /**
     *
     * @param audio name of the file (without .wav file ending)
     * @param state
     * @param shift in ticks
     * @param delay for syncing purposes, if not used, pass null as value
     * @return false if the file is null or empty
     */
    public boolean play(String audio, AudioState state, int shift, @Nullable LatencyTimer delay)
    {
        AudioFile file = this.files.get(audio);

        float elapsed = 0;

        if (file == null || file.canBeUpdated())
        {
            file = this.load(audio, new File(this.folder, audio + ".wav"));

            /* Account for networking and loading delays */
            if (delay != null)
            {
                elapsed = delay.getElapsedTime() / 1000F;
            }
        }

        if (file == null || file.isEmpty())
        {
            return false;
        }

        WavePlayer player = file.player;

        float seconds = shift / 20F + elapsed;

        if (state == AudioState.REWIND)
        {
            player.stop();
            player.play();
            player.setPlaybackPosition(seconds);
        }
        else if (state == AudioState.PAUSE)
        {
            player.pause();
        }
        else if (state == AudioState.PAUSE_SET)
        {
            if (player.isStopped())
            {
                player.play();
            }

            player.pause();
            player.setPlaybackPosition(seconds);
        }
        else if (state == AudioState.RESUME)
        {
            player.play();
        }
        else if (state == AudioState.RESUME_SET)
        {
            player.play();
            player.setPlaybackPosition(seconds);
        }
        else if (state == AudioState.SET)
        {
            player.setPlaybackPosition(seconds);
        }
        else if (state == AudioState.STOP)
        {
            player.stop();
        }

        return true;
    }

    public void reset()
    {
        for (AudioFile file : this.files.values())
        {
            file.delete();
        }

        this.files.clear();
    }

    public void pause(boolean pause)
    {
        for (AudioFile file : this.files.values())
        {
            file.pause(pause);
        }
    }
}