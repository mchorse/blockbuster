package mchorse.blockbuster.recording.sounds;

import java.util.ArrayList;
import java.util.List;

public class SoundSession
{
    public List<SoundRecord> sounds = new ArrayList<SoundRecord>();

    public void record(int frame, String string, float volume, float pitch)
    {
        this.sounds.add(new SoundRecord(frame, string, volume, pitch));
    }
}