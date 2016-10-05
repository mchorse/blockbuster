package mchorse.blockbuster.recording;

import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.recording.RecordPlayer.Mode;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Record recorder class
 *
 * This thing is responsible for recording a record. Yeah, kinda funky naming,
 * but it is the <s>not</s> best naming, eva!
 */
public class RecordRecorder
{
    /**
     * Record
     */
    public Record record;

    /**
     * List of actions which will be saved every time
     */
    public List<Action> actions = new ArrayList<Action>();

    public Mode mode;
    public int ticks = 0;

    public RecordRecorder(Record record, Mode mode)
    {
        this.record = record;
        this.mode = mode;
    }

    /**
     * Record frame from the player
     */
    public void record(EntityPlayer player)
    {
        boolean both = this.mode == Mode.BOTH;

        if (this.mode == Mode.FRAMES || both)
        {
            Frame frame = new Frame();

            frame.fromPlayer(player);
            this.record.frames.add(frame);
        }

        if (this.mode == Mode.ACTIONS || both)
        {
            this.record.actions.add(this.actions.isEmpty() ? null : this.actions.remove(0));
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