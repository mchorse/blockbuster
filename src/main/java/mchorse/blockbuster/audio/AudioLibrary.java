package mchorse.blockbuster.audio;

import mchorse.mclib.utils.wav.Wave;
import mchorse.mclib.utils.wav.WavePlayer;
import mchorse.mclib.utils.wav.WaveReader;
import mchorse.mclib.utils.wav.Waveform;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
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

	public void reload()
	{
		File[] files = this.folder.listFiles();

		if (files == null)
		{
			return;
		}

		for (File file : files)
		{
			if (!file.getName().endsWith(".wav"))
			{
				continue;
			}

			String name = file.getName();
			long lastModified = file.lastModified();

			name = name.substring(0, name.length() - 4);

			AudioFile last = this.files.get(name);

			if (last != null && last.update >= lastModified)
			{
				continue;
			}

			try
			{
				Wave wave = new WaveReader().read(new FileInputStream(file));

				if (wave.getBytesPerSample() > 2)
				{
					wave = wave.convertTo16();
				}

				WavePlayer player = new WavePlayer().initialize(wave);
				Waveform waveform = new Waveform();
				waveform.populate(wave, 10, 40);

				this.files.put(name, new AudioFile(player, waveform, lastModified));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public boolean play(String audio, AudioState state, int shift)
	{
		AudioFile file = this.files.get(audio);

		if (file == null)
		{
			return false;
		}

		WavePlayer player = file.player;

		float seconds = shift / 20F;

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
}