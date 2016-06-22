package noname.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

import net.minecraft.entity.Entity;
import noname.blockbuster.entity.ActorEntity;
import noname.blockbuster.recording.Mocap;

/**
 * Mounting action
 *
 * This actor makes actor to mount or unmount an entity by UUID.
 * I should probably move from UUID to using a type of entity withing given
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
    public void apply(ActorEntity actor)
    {
        Entity mount = Mocap.entityByUUID(actor.worldObj, this.target);

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
    public void fromBytes(DataInput in) throws IOException
    {
        super.fromBytes(in);

        this.target = new UUID(in.readLong(), in.readLong());
        this.isMounting = in.readBoolean();
    }

    @Override
    public void toBytes(DataOutput out) throws IOException
    {
        super.toBytes(out);

        out.writeLong(this.target.getMostSignificantBits());
        out.writeLong(this.target.getLeastSignificantBits());
        out.writeBoolean(this.isMounting);
    }
}
