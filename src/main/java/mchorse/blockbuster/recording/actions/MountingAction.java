package mchorse.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Mounting action
 *
 * This actor makes actor to mount or unmount an entity by UUID.
 * I should probably move from UUID to using a type of entity with given
 * radius (3-5 blocks).
 */
public class MountingAction extends Action
{
    public UUID target;
    public boolean isMounting;

    public MountingAction()
    {}

    public MountingAction(UUID target, boolean isMounting)
    {
        this.target = target;
        this.isMounting = isMounting;
    }

    @Override
    public byte getType()
    {
        return Action.MOUNTING;
    }

    @Override
    public void apply(EntityActor actor)
    {
        Entity mount = EntityUtils.entityByUUID(actor.worldObj, this.target);

        if (mount == null)
        {
            return;
        }

        if (this.isMounting)
        {
            actor.ridingEntity = mount;
        }
        else
        {
            actor.ridingEntity = null;
        }
    }

    @Override
    public void fromBytes(DataInput in) throws IOException
    {
        this.target = new UUID(in.readLong(), in.readLong());
        this.isMounting = in.readBoolean();
    }

    @Override
    public void toBytes(DataOutput out) throws IOException
    {
        out.writeLong(this.target.getMostSignificantBits());
        out.writeLong(this.target.getLeastSignificantBits());
        out.writeBoolean(this.isMounting);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.target = new UUID(tag.getLong("Most"), tag.getLong("Least"));
        this.isMounting = tag.getBoolean("Mounting");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        tag.setLong("Most", this.target.getMostSignificantBits());
        tag.setLong("Least", this.target.getLeastSignificantBits());
        tag.setBoolean("Mounting", this.isMounting);
    }
}