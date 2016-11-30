package mchorse.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.data.Frame;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Shoot arrow action
 *
 * This action shoots emulates arrow shooting. This would look confusing when
 * the actor lack of bow, he would be like freaking arrow mage or something.
 */
public class ShootArrowAction extends Action
{
    public int charge;

    public ShootArrowAction()
    {}

    public ShootArrowAction(int charge)
    {
        this.charge = charge;
    }

    @Override
    public byte getType()
    {
        return Action.SHOOT_ARROW;
    }

    /**
     * Some code in this method is borrowed from ItemBow, I guess, I don't
     * remember
     */
    @Override
    public void apply(EntityActor actor)
    {
        World world = actor.worldObj;
        Frame frame = actor.playback.record.frames.get(actor.playback.tick);

        float f = this.getArrowVelocity(this.charge);
        EntityArrow arrow = new EntityArrow(world, actor, this.charge);

        this.setAim(arrow, actor, frame.pitch, frame.yaw, 0.0F, f * 3.0F, 1.0F);
        world.spawnEntityInWorld(arrow);
    }

    private void setAim(EntityArrow arrow, Entity entity, float pitch, float yaw, float something, float velocity, float inaccuracy)
    {
        float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        float f1 = -MathHelper.sin(pitch * 0.017453292F);
        float f2 = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        arrow.setThrowableHeading(f, f1, f2, velocity, inaccuracy);
        arrow.motionX += entity.motionX;
        arrow.motionZ += entity.motionZ;

        if (!entity.onGround)
        {
            arrow.motionY += entity.motionY;
        }
    }

    private float getArrowVelocity(int charge)
    {
        float f = charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;

        if (f > 1.0F)
        {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public void fromBytes(DataInput in) throws IOException
    {
        this.charge = in.readInt();
    }

    @Override
    public void toBytes(DataOutput out) throws IOException
    {
        out.writeInt(this.charge);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.charge = tag.getByte("Charge");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        tag.setByte("Charge", (byte) this.charge);
    }
}