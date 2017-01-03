package mchorse.blockbuster.recording.actions;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

/**
 * Attack action
 *
 * This action is responsible for attacking an entity in in front of the
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
        Entity target = EntityUtils.getTargetEntity(actor, 5.0);

        if (target != null)
        {
            target.attackEntityFrom(DamageSource.causeMobDamage(actor), 2.0F);
        }
    }
}