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
        if (actor.morph != null)
        {
            Frame frame = actor.playback.getCurrentFrame();

            float yaw = actor.rotationYaw;
            float yawHead = actor.rotationYaw;
            float pitch = actor.rotationPitch;

            float prevYaw = actor.prevRotationYaw;
            float prevYawHead = actor.prevRotationYawHead;
            float prevPitch = actor.prevRotationPitch;

            actor.rotationYaw = actor.prevRotationYaw = frame.yaw;
            actor.rotationYawHead = actor.prevRotationYawHead = frame.yawHead;
            actor.rotationPitch = actor.prevRotationPitch = frame.pitch;

            actor.morph.action(actor);

            actor.rotationYaw = yaw;
            actor.rotationYawHead = yawHead;
            actor.rotationPitch = pitch;

            actor.prevRotationYaw = prevYaw;
            actor.prevRotationYawHead = prevYawHead;
            actor.prevRotationPitch = prevPitch;
        }
    }
}