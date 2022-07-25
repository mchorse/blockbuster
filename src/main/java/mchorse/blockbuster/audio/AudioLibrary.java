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
     * @param state play, pause, resume etc. the audio
     * @param shift in ticks
     * @param delay for syncing purposes, if not used, pass null as value
     * @return false if the file is null or empty
     */
    public boolean handleAudio(String audio, AudioState state, int shift, @Nullable LatencyTimer delay)
    {
        AudioFile file = this.files.get(audio);

        if (file == null || file.canBeUpdated())
        {
            file = this.load(audio, new File(this.folder, audio + ".wav"));
        }

        if (file == null || file.isEmpty())
        {
            return false;
        }

        WavePlayer player = file.player;

        float seconds = shift / 20F;
        float elapsedDelay = (delay != null) ? delay.getElapsedTime() / 1000F : 0F;

        System.out.println("Received audio latency of: " + elapsedDelay + " seconds");

        this.handleAudioState(state, player, seconds, elapsedDelay);

        return true;
    }

    private void handleAudioState(AudioState state, WavePlayer player, float seconds, float elapsedDelay)
    {
        switch (state)
        {
            case REWIND:
                player.stop();
                player.play();
                player.setPlaybackPosition(seconds + elapsedDelay);

                break;
            case PAUSE:
                elapsedDelay = (player.isPlaying()) ? elapsedDelay : 0;

                player.pause();
                player.setPlaybackPosition(((seconds == 0) ? player.getPlaybackPosition() : seconds) - elapsedDelay);

                break;
            case PAUSE_SET:
                if (player.isStopped())
                {
                    player.play();
                }

                player.pause();
                player.setPlaybackPosition(seconds);

                break;
            case RESUME:
                player.play();
                player.setPlaybackPosition(player.getPlaybackPosition() + elapsedDelay);

                break;
            case RESUME_SET:
                player.play();
                player.setPlaybackPosition(seconds + elapsedDelay);

                break;
            case SET:
                elapsedDelay = (player.isPlaying()) ? elapsedDelay : 0;

                player.setPlaybackPosition(seconds + elapsedDelay);

                break;
            case STOP:
                player.stop();

                break;
        }
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