package mchorse.blockbuster.recording;

import mchorse.blockbuster.recording.RecordPlayer.Mode;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Record recorder class
 *
 * This thing is responsible for recording a record. Yeah, kinda funky naming,
 * but it is the <s>not</s> best naming, eva!
 */
public class RecordRecorder
{
    public Record record;

    public Mode mode;
    public int ticks = 0;

    public RecordRecorder(Record record, Mode mode)
    {
        this.mode = mode;
    }

    /**
     * Check if the record player is finished
     */
    public boolean isFinished()
    {
        return this.ticks >= this.record.getLength();
    }

    /**
     * Record frame from the player
     */
    public void record(EntityPlayer player)
    {
        if (this.isFinished())
        {
            return;
        }

        if (this.mode == Mode.FRAMES || this.mode == Mode.BOTH)
        {
            Frame frame = new Frame();

            frame.fromPlayer(player);
            this.record.frames.add(frame);
        }

        this.ticks++;
    }

    /**
     * Checks if this recorder recording actions
     */
    public boolean isRecordingActions()
    {
        return this.mode == Mode.ACTIONS || this.mode == Mode.BOTH;
    }
}