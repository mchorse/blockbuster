package mchorse.blockbuster.recording;

import mchorse.blockbuster.common.entity.EntityActor;

/**
 * Record player class
 *
 * This thing is responsible for playing given record.
 */
public class RecordPlayer
{
    /* Source of playing */
    public Record record;

    public Mode mode;
    public int ticks = 0;
    public boolean kill = false;

    public RecordPlayer(Record record, Mode mode)
    {
        this.record = record;
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
     * Apply current frame and advance to the next one
     */
    public void next(EntityActor actor)
    {
        if (this.isFinished())
        {
            return;
        }

        boolean both = this.mode == Mode.BOTH;

        if (this.mode == Mode.ACTIONS || both) this.record.applyAction(this.ticks, actor);
        if (this.mode == Mode.FRAMES || both) this.record.applyFrame(this.ticks, actor);

        this.ticks++;
    }

    public void rewind()
    {
        this.ticks = 0;
    }

    /**
     * Mode enumeration. This enumeration represents how to playback the
     * record. Not really sure if BOTH is going to be used at all, but ACTIONS
     * and FRAMES definitely would.
     */
    public enum Mode
    {
        ACTIONS, FRAMES, BOTH;
    }
}