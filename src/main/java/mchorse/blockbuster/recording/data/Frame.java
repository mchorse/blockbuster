package mchorse.blockbuster.recording.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import mchorse.blockbuster.common.entity.EntityActor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

/**
 * Recording frame class
 *
 * This class stores state data about the player in the specific frame that was
 * captured.
 */
public class Frame
{
    /* Position */
    public double x;
    public double y;
    public double z;

    /* Rotation */
    public float yaw;
    public float yawHead;
    public float pitch;

    /* Mount's data */
    public float mountYaw;
    public float mountPitch;

    public boolean isMounted;

    /* Motion */
    public double motionX;
    public double motionY;
    public double motionZ;

    /* Fall distance */
    public float fallDistance;

    /* Entity flags */
    public boolean isAirBorne;
    public boolean isSneaking;
    public boolean isSprinting;
    public boolean onGround;
    public boolean flyingElytra;

    /* Active hand */
    public int activeHands;

    /* Methods for retrieving/applying state data */

    /**
     * Set frame fields from player entity.
     */
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

        /* Active hands */
        this.activeHands = player.isHandActive() ? (player.getActiveHand() == EnumHand.OFF_HAND ? 2 : 1) : 0;
    }

    /**
     * Apply frame properties on actor. Different actions will be made
     * depending on which side this method was invoked.
     *
     * Use second argument to force things to be .
     */
    public void applyOnActor(EntityActor actor, boolean force)
    {
        Entity mount = actor.isRiding() ? actor.getRidingEntity() : actor;

        if (mount instanceof EntityActor)
        {
            mount = actor;
        }

        boolean isRemote = actor.worldObj.isRemote;

        /* This is most important part of the code that makes the recording
         * super smooth.
         *
         * By the way, this code is useful only on the client side, for more
         * reference see renderer classes (they use prev* and lastTick* stuff
         * for interpolation).
         */
        if (this.isMounted)
        {
            mount.prevRotationYaw = mount.rotationYaw;
            mount.prevRotationPitch = mount.rotationPitch;
        }

        actor.prevRotationYaw = actor.rotationYaw;
        actor.prevRotationPitch = actor.rotationPitch;
        actor.prevRotationYawHead = actor.rotationYawHead;

        /* Inject frame's values into actor */
        mount.setPosition(this.x, this.y, this.z);

        /* Rotation */
        if (this.isMounted)
        {
            mount.rotationYaw = this.mountYaw;
            mount.rotationPitch = this.mountPitch;
        }

        actor.rotationYaw = this.yaw;
        actor.rotationPitch = this.pitch;
        actor.rotationYawHead = this.yawHead;

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

        if (!isRemote)
        {
            if (this.activeHands > 0 && !actor.isHandActive())
            {
                actor.setActiveHand(this.activeHands == 1 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
            }
            else if (this.activeHands == 0 && actor.isHandActive())
            {
                actor.stopActiveHand();
            }
        }
    }

    /* Save/load frame instance */

    /**
     * Write frame data to an output stream. Used purely for data
     * transportation over the network.
     */
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

        out.writeByte(this.activeHands);
    }

    /**
     * Read frame data from input stream. Used purely for data transportation
     * over the network.
     */
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

        this.activeHands = in.readByte();
    }

    /**
     * Write frame data to NBT tag. Used for saving the frame on the disk.
     *
     * This is probably going to be extracted in the future to support
     * compatibility, but I don't really know since writing the data happens
     * in one format, while reading is in different versions.
     */
    public void toNBT(NBTTagCompound tag)
    {
        tag.setFloat("X", (float) this.x);
        tag.setFloat("Y", (float) this.y);
        tag.setFloat("Z", (float) this.z);

        tag.setFloat("MX", (float) this.motionX);
        tag.setFloat("MY", (float) this.motionX);
        tag.setFloat("MZ", (float) this.motionX);

        tag.setFloat("RX", this.yaw);
        tag.setFloat("RY", this.pitch);
        tag.setFloat("RZ", this.yawHead);

        if (this.isMounted)
        {
            tag.setFloat("MRX", this.mountYaw);
            tag.setFloat("MRY", this.mountPitch);
        }

        tag.setFloat("Fall", this.fallDistance);

        tag.setBoolean("Airborne", this.isAirBorne);
        tag.setBoolean("Elytra", this.flyingElytra);
        tag.setBoolean("Sneaking", this.isSneaking);
        tag.setBoolean("Sprinting", this.isSprinting);
        tag.setBoolean("Ground", this.onGround);

        if (this.activeHands > 0)
        {
            tag.setByte("Hands", (byte) this.activeHands);
        }
    }

    /**
     * Read frame data from NBT tag. Used for loading frame from disk.
     *
     * This is going to be extracted in the future to support compatibility.
     */
    public void fromNBT(NBTTagCompound tag)
    {
        this.x = tag.getFloat("X");
        this.y = tag.getFloat("Y");
        this.z = tag.getFloat("Z");

        this.motionX = tag.getFloat("MX");
        this.motionY = tag.getFloat("MY");
        this.motionZ = tag.getFloat("MZ");

        this.yaw = tag.getFloat("RX");
        this.pitch = tag.getFloat("RY");
        this.yawHead = tag.getFloat("RZ");

        if (tag.hasKey("MRX") && tag.hasKey("MRY"))
        {
            this.isMounted = true;
            this.mountYaw = tag.getFloat("MRX");
            this.mountPitch = tag.getFloat("MRY");
        }

        this.fallDistance = tag.getFloat("Fall");

        this.isAirBorne = tag.getBoolean("Airborne");
        this.flyingElytra = tag.getBoolean("Elytra");
        this.isSneaking = tag.getBoolean("Sneaking");
        this.isSprinting = tag.getBoolean("Sprinting");
        this.onGround = tag.getBoolean("Ground");

        if (tag.hasKey("Hands"))
        {
            this.activeHands = tag.getByte("Hands");
        }
    }
}