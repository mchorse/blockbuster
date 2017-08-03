package mchorse.blockbuster.recording.sounds;

import java.util.ArrayList;
import java.util.List;

public class SoundSession
{
    public List<SoundRecord> sounds = new ArrayList<SoundRecord>();
    public int frame;

    public void record(String string, float volume, float pitch)
    {
        this.sounds.add(new SoundRecord(this.frame, string, volume, pitch));
    }
}