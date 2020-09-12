package mchorse.blockbuster.audio;

import mchorse.mclib.utils.wav.WavePlayer;
import mchorse.mclib.utils.wav.Waveform;

public class AudioFile
{
	public String name;
	public WavePlayer player;
	public Waveform waveform;
	public long update;

	public AudioFile(String name, WavePlayer player, Waveform waveform, long update)
	{
		this.name = name;
		this.player = player;
		this.waveform = waveform;
		this.update = update;
	}
}