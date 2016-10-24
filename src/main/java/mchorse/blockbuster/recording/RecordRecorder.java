package mchorse.blockbuster.recording;

import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Record recorder class
 *
 * This thing is responsible for recording a record. It can record actions and
 * frames to the given recorder.
 *
 * Yeah, kinda funky naming, but it is the <s>not</s> best naming, eva!
 */
public class RecordRecorder
{
    public Record record;

    /**
     * List of actions which will be saved every time {@link #record(EntityPlayer)}
     * method is invoked.
     */
    public List<Action> actions = new ArrayList<Action>();

    public Mode mode;
    public int ticks = 0;
    public int delay = 1;
    public PlayerTracker tracker;

    public RecordRecorder(Record record, Mode mode)
    {
        this.record = record;
        this.mode = mode;

        if (mode == Mode.ACTIONS || mode == Mode.BOTH)
        {
            this.tracker = new PlayerTracker(this);
        }
    }

    /**
     * Record frame from the player
     */
    public void record(EntityPlayer player)
    {
        boolean both = this.mode == Mode.BOTH;

        if (this.delay-- > 0)
        {
            return;
        }

        if (this.mode == Mode.FRAMES || both)
        {
            Frame frame = new Frame();

            frame.fromPlayer(player);
            this.record.frames.add(frame);
        }

        if (this.mode == Mode.ACTIONS || both)
        {
            this.tracker.track(player);
            this.record.actions.add(this.actions.isEmpty() ? null : this.actions.remove(0));
        }

        this.ticks++;
        this.delay = this.record.delay;
    }
}