package mchorse.blockbuster.recording.actions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

/**
 * Damage action
 *
 * This action is responsible for dealing damage to the actor. Currently used
 * for killing the actor.
 */
public class DamageAction extends Action
{
    public float damage;

    public DamageAction()
    {}

    public DamageAction(float damage)
    {
        this.damage = damage;
    }

    @Override
    public byte getType()
    {
        return Action.DAMAGE;
    }

    @Override
    public void apply(EntityLivingBase actor)
    {
        actor.attackEntityFrom(DamageSource.OUT_OF_WORLD, this.damage);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.damage = tag.getFloat("Damage");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        tag.setFloat("Damage", this.damage);
    }
}