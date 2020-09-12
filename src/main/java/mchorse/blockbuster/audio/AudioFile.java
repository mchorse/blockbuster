package mchorse.blockbuster.audio;

import mchorse.mclib.utils.wav.WavePlayer;
import mchorse.mclib.utils.wav.Waveform;

public class AudioFile
{
	public WavePlayer player;
	public Waveform waveform;
	public long update;

	public AudioFile(WavePlayer player, Waveform waveform, long update)
	{
		this.player = player;
		this.waveform = waveform;
		this.update = update;
	}
}