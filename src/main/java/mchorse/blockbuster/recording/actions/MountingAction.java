package mchorse.blockbuster.recording.actions;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
    /**
     * Default UUID 
     */
    public static final UUID DEFAULT = new UUID(0, 0);

    public UUID target = DEFAULT;
    public boolean isMounting;

    public MountingAction()
    {}

    public MountingAction(UUID target, boolean isMounting)
    {
        this.target = target;
        this.isMounting = isMounting;
    }

    @Override
    public void apply(EntityLivingBase actor)
    {
        Entity mount = EntityUtils.entityByUUID(actor.worldObj, this.target);

        if (mount == null)
        {
            Frame frame = EntityUtils.getRecordPlayer(actor).getCurrentFrame();

            if (frame == null) return;

            float yaw = actor.rotationYaw;
            float pitch = actor.rotationPitch;
            float yawHead = actor.rotationYawHead;

            actor.rotationYaw = frame.yaw;
            actor.rotationPitch = frame.pitch;
            actor.rotationYawHead = frame.yawHead;

            mount = EntityUtils.getTargetEntity(actor, 5.0);

            actor.rotationYaw = yaw;
            actor.rotationPitch = pitch;
            actor.rotationYawHead = yawHead;
        }

        if (mount == null && this.isMounting)
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
    public void fromBuf(ByteBuf buf)
    {
        super.fromBuf(buf);
        this.target = new UUID(buf.readLong(), buf.readLong());
        this.isMounting = buf.readBoolean();
    }

    @Override
    public void toBuf(ByteBuf buf)
    {
        super.toBuf(buf);
        buf.writeLong(this.target.getMostSignificantBits());
        buf.writeLong(this.target.getLeastSignificantBits());
        buf.writeBoolean(this.isMounting);
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