package mchorse.blockbuster.recording.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import mchorse.blockbuster.common.entity.EntityActor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Recording frame class
 *
 * This class stores state data about the player in the specific frame that was
 * captured.
 */
public class Frame
{
    public double x;
    public double y;
    public double z;

    public float yaw;
    public float yawHead;
    public float pitch;

    public float mountYaw;
    public float mountPitch;

    public boolean isMounted;

    public double motionX;
    public double motionY;
    public double motionZ;

    public float fallDistance;

    public boolean isAirBorne;
    public boolean isSneaking;
    public boolean isSprinting;
    public boolean onGround;
    public boolean flyingElytra;

    /* Methods for retrieving/applying state data */

    public void fromPlayer(EntityPlayer player)
    {
        Entity mount = player.isRiding() ? player.getRidingEntity() : player;

        /* Position and rotation */
        this.x = mount.posX;
        this.y = mount.posY;
        this.z = mount.posZ;

        this.yaw = player.rotationYaw;
        this.yawHead = player.rotationYawHead;
        this.pitch = player.rotationPitch;

        /* Mount information */
        this.isMounted = mount != player;

        if (this.isMounted)
        {
            this.mountYaw = mount.rotationYaw;
            this.mountPitch = mount.rotationPitch;
        }

        /* Motion and fall distance */
        this.motionX = mount.motionX;
        this.motionY = mount.motionY;
        this.motionZ = mount.motionZ;

        this.fallDistance = mount.fallDistance;

        /* States */
        this.isSprinting = mount.isSprinting();
        this.isSneaking = player.isSneaking();
        this.flyingElytra = player.isElytraFlying();

        this.isAirBorne = mount.isAirBorne;
        this.onGround = mount.onGround;
    }

    public void applyOnActor(EntityActor actor, boolean force)
    {
        Entity mount = actor.isRiding() ? actor.getRidingEntity() : actor;

        boolean isRemote = actor.worldObj.isRemote;

        /* This is most important part of the code that makes the recording
         * super smooth.
         *
         * By the way, this code is useful only on the client side, for more
         * reference see renderer classes (they use prev* and lastTick* stuff
         * for interpolation).
         */
        if (isRemote)
        {
            mount.prevPosX = mount.posX;
            mount.prevPosY = mount.posY;
            mount.prevPosZ = mount.posZ;
        }

        if (this.isMounted)
        {
            mount.prevRotationYaw = mount.rotationYaw;
            mount.prevRotationPitch = mount.rotationPitch;
        }

        actor.prevRotationYaw = actor.rotationYaw;
        actor.prevRotationPitch = actor.rotationPitch;
        actor.prevRotationYawHead = actor.rotationYawHead;

        /* Inject frame's values into actor */
        if (!isRemote || force)
        {
            mount.setPosition(this.x, this.y, this.z);
        }

        /* Rotation */
        if (isRemote || force)
        {
            if (this.isMounted)
            {
                mount.rotationYaw = this.mountYaw;
                mount.rotationPitch = this.mountPitch;
            }

            actor.rotationYaw = this.yaw;
            actor.rotationPitch = this.pitch;
            actor.rotationYawHead = this.yawHead;
        }

        /* Motion and fall distance */
        mount.motionX = this.motionX;
        mount.motionY = this.motionY;
        mount.motionZ = this.motionZ;

        mount.fallDistance = this.fallDistance;

        /* Booleans */
        if (!isRemote || force)
        {
            mount.setSprinting(this.isSprinting);
            actor.setSneaking(this.isSneaking);
            actor.setElytraFlying(this.flyingElytra);
        }

        mount.isAirBorne = this.isAirBorne;
        mount.onGround = this.onGround;
    }

    /* Save/load frame instance */

    public void toBytes(DataOutput out) throws IOException
    {
        out.writeFloat((float) this.x);
        out.writeFloat((float) this.y);
        out.writeFloat((float) this.z);

        out.writeFloat(this.yaw);
        out.writeFloat(this.yawHead);
        out.writeFloat(this.pitch);

        out.writeBoolean(this.isMounted);

        if (this.isMounted)
        {
            out.writeFloat(this.mountYaw);
            out.writeFloat(this.mountPitch);
        }

        out.writeFloat((float) this.motionX);
        out.writeFloat((float) this.motionY);
        out.writeFloat((float) this.motionZ);

        out.writeFloat(this.fallDistance);

        out.writeBoolean(this.isAirBorne);
        out.writeBoolean(this.isSneaking);
        out.writeBoolean(this.isSprinting);
        out.writeBoolean(this.onGround);
        out.writeBoolean(this.flyingElytra);
    }

    public void fromBytes(DataInput in) throws IOException
    {
        this.x = in.readFloat();
        this.y = in.readFloat();
        this.z = in.readFloat();

        this.yaw = in.readFloat();
        this.yawHead = in.readFloat();
        this.pitch = in.readFloat();

        this.isMounted = in.readBoolean();

        if (this.isMounted)
        {
            this.mountYaw = in.readFloat();
            this.mountPitch = in.readFloat();
        }

        this.motionX = in.readFloat();
        this.motionY = in.readFloat();
        this.motionZ = in.readFloat();

        this.fallDistance = in.readFloat();

        this.isAirBorne = in.readBoolean();
        this.isSneaking = in.readBoolean();
        this.isSprinting = in.readBoolean();
        this.onGround = in.readBoolean();
        this.flyingElytra = in.readBoolean();
    }
}