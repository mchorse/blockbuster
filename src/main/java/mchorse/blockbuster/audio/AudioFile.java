package mchorse.blockbuster.audio;

import mchorse.mclib.utils.wav.WavePlayer;
import mchorse.mclib.utils.wav.Waveform;

import java.io.File;

public class AudioFile
{
	public String name;
	public File file;
	public WavePlayer player;
	public Waveform waveform;
	public long update;

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
}