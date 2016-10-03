package mchorse.blockbuster.recording;

import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.actions.Action;

/**
 * This class stores actions and frames states for a recording (to be playbacked
 * or while recording).
 *
 * There's two list arrays in this class, index in both of these arrays
 * represents the frame position (0 is first frame). Frames list is always
 * populated, but actions list will contain some nulls.
 */
public class Record
{
    public List<Action> actions = new ArrayList<Action>();
    public List<Frame> frames = new ArrayList<Frame>();

    /**
     * Apply a frame at given tick on the given actor. Don't pass tick value
     * less than 0, otherwise you might experience <s>tranquility</s> game
     * crash.
     */
    public void applyFrame(int tick, EntityActor actor)
    {
        if (tick > this.frames.size())
        {
            return;
        }

        this.frames.get(tick).applyOnActor(actor);
    }

    /**
     * Apply an action at the given tick on the given actor. Don't pass tick
     * value less than 0, otherwise you might experience game crash.
     */
    public void applyAction(int tick, EntityActor actor)
    {
        if (tick > this.actions.size())
        {
            return;
        }

        Action action = this.actions.get(tick);

        if (action != null)
        {
            action.apply(actor);
        }
    }
}