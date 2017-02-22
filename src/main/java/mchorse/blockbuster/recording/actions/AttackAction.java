package mchorse.blockbuster.recording.actions;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

/**
 * Attack action
 *
 * This action is responsible for attacking an entity in in front of the actor.
 */
public class AttackAction extends Action
{
    public AttackAction()
    {}

    @Override
    public byte getType()
    {
        return Action.ATTACK;
    }

    @Override
    public void apply(EntityActor actor)
    {
        Frame frame = actor.playback.record.frames.get(actor.playback.tick);
        float yaw = actor.rotationYaw;
        float pitch = actor.rotationPitch;
        float yawHead = actor.rotationYawHead;

        actor.rotationYaw = frame.yaw;
        actor.rotationPitch = frame.pitch;
        actor.rotationYawHead = frame.yawHead;

        Entity target = EntityUtils.getTargetEntity(actor, 5.0);

        actor.rotationYaw = yaw;
        actor.rotationPitch = pitch;
        actor.rotationYawHead = yawHead;

        if (target != null)
        {
            target.attackEntityFrom(DamageSource.causeMobDamage(actor), 2.0F);

            if (actor.morph != null)
            {
                actor.morph.attack(target, actor);
            }
        }
    }
}