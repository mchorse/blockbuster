package mchorse.blockbuster.recording.actions;

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
            actor.startRiding(mount);
        }
        else
        {
            actor.dismountRidingEntity();
        }
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