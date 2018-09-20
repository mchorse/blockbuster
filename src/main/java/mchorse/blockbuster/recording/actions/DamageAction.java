package mchorse.blockbuster.recording.actions;

import io.netty.buffer.ByteBuf;
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
    public void apply(EntityLivingBase actor)
    {
        actor.attackEntityFrom(DamageSource.outOfWorld, this.damage);
    }

    @Override
    public void fromBuf(ByteBuf buf)
    {
        super.fromBuf(buf);
        this.damage = buf.readFloat();
    }

    @Override
    public void toBuf(ByteBuf buf)
    {
        super.toBuf(buf);
        buf.writeFloat(this.damage);
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