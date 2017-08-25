package mchorse.blockbuster.recording.sounds;

public class SoundRecord
{
    public int frame;
    public float volume;
    public float pitch;
    public String name;

    public SoundRecord(int frame, String name, float volume, float pitch)
    {
        this.frame = frame;
        this.name = name;
        this.volume = volume;
        this.pitch = pitch;
    }
}