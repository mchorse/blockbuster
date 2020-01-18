package mchorse.blockbuster.recording;

import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.capturing.PlayerTracker;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

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
    /**
     * Initial record
     */
    public Record record;

    /**
     * List of actions which will be saved every time {@link #record(EntityPlayer)}
     * method is invoked.
     */
    public List<Action> actions = new ArrayList<Action>();

    /**
     * Recording mode (record actions, frames or both)
     */
    public Mode mode;

    /**
     * Current recording tick
     */
    public int tick = 0;

    /**
     * Used by camera marker
     */
    public int previousTick = 0;

    /**
     * Delay between recording ticks
     */
    public int delay = 1;

    /**
     * Whether recorded player should be teleported back
     */
    public boolean teleportBack;

    /**
     * First frame (to restore the position)
     */
    private Frame first;

    /**
     * Player tracker, this dude is responsible for tracking inventory slots,
     * swing progress and elytra flying updates
     */
    public PlayerTracker tracker;

    public RecordRecorder(Record record, Mode mode, EntityPlayer player, boolean teleportBack)
    {
        this.record = record;
        this.mode = mode;
        this.teleportBack = teleportBack;
        this.first = new Frame();
        this.first.fromPlayer(player);

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
        if (--this.delay > 0)
        {
            return;
        }

        boolean both = this.mode == Mode.BOTH;

        if (this.mode == Mode.FRAMES || both)
        {
            Frame frame = new Frame();

            frame.fromPlayer(player);
            this.record.frames.add(frame);
        }

        if (this.mode == Mode.ACTIONS || both)
        {
            this.tracker.track(player);

            List<Action> list = null;

            if (!this.actions.isEmpty())
            {
                list = new ArrayList<Action>();
                list.addAll(this.actions);

                this.actions.clear();
            }

            this.record.actions.add(list);
        }

        this.tick++;
        this.delay = this.record.delay;
    }

	public void stop(EntityPlayer player)
    {
        if (this.teleportBack && player instanceof EntityPlayer)
        {
            ((EntityPlayerMP) player).connection.setPlayerLocation(this.first.x, this.first.y, this.first.z, this.first.yaw, this.first.pitch);
        }
	}
}