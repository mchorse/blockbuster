package mchorse.blockbuster.recording;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Scheduled recorder class
 */
public class ScheduledRecording
{
    public RecordRecorder recorder;
    public EntityPlayer player;
    public Runnable runnable;
    public int countdown;
    public int offset;

    public ScheduledRecording(RecordRecorder recorder, EntityPlayer player, Runnable runnable, int countdown, int offset)
    {
        this.recorder = recorder;
        this.player = player;
        this.runnable = runnable;
        this.countdown = countdown;
        this.offset = offset;
    }

    public void run()
    {
        if (this.runnable != null)
        {
            this.runnable.run();
        }
    }
}
