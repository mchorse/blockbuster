package mchorse.blockbuster.recording.actions;

import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.utils.EntityUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
    public void apply(EntityLivingBase actor)
    {
        Frame frame = EntityUtils.getRecordPlayer(actor).getCurrentFrame();
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

            AbstractMorph morph = mchorse.metamorph.api.EntityUtils.getMorph(actor);

            if (morph != null)
            {
                morph.attack(target, actor);
            }
        }
    }
}