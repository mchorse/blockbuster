package mchorse.blockbuster.recording.actions;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.data.Frame;

/**
 * Morph's action action
 *
 * This method is responsible for executing morph's action, if it has one.
 */
public class MorphActionAction extends Action
{
    public MorphActionAction()
    {}

    @Override
    public byte getType()
    {
        return MORPH_ACTION;
    }

    @Override
    public void apply(EntityActor actor)
    {
        Frame frame = actor.playback.record.frames.get(actor.playback.tick);
        float yaw = actor.rotationYaw;
        float pitch = actor.rotationPitch;

        actor.rotationYaw = frame.yaw;
        actor.rotationPitch = frame.pitch;

        if (actor.morph != null)
        {
            actor.morph.action(actor);
        }

        actor.rotationYaw = yaw;
        actor.rotationPitch = pitch;
    }
}