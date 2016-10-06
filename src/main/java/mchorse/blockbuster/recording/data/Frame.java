package mchorse.blockbuster.recording.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import mchorse.blockbuster.common.entity.EntityActor;
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
        this.x = player.posX;
        this.y = player.posY;
        this.z = player.posZ;

        this.yaw = player.rotationYaw;
        this.yawHead = player.rotationYawHead;
        this.pitch = player.rotationPitch;

        this.motionX = player.motionX;
        this.motionY = player.motionY;
        this.motionZ = player.motionZ;

        this.fallDistance = player.fallDistance;

        this.isAirBorne = player.isAirBorne;
        this.isSneaking = player.isSneaking();
        this.isSprinting = player.isSprinting();
        this.onGround = player.onGround;
        this.flyingElytra = player.isElytraFlying();
    }

    public void applyOnActor(EntityActor actor)
    {
        /* This is most important part of the code that makes the recording
         * super smooth.
         *
         * By the way, this code is useful only on the client side, for more
         * reference see renderer classes (they use prev* and lastTick* stuff
         * for interpolation).
         */
        actor.prevPosX = actor.lastTickPosX = actor.posX;
        actor.prevPosY = actor.lastTickPosY = actor.posY;
        actor.prevPosZ = actor.lastTickPosZ = actor.posZ;

        actor.prevRotationYaw = actor.rotationYaw;
        actor.prevRotationYawHead = actor.rotationYawHead;
        actor.prevRotationPitch = actor.rotationPitch;

        /* Inject frame's values into actor */
        actor.posX = this.x;
        actor.posY = this.y;
        actor.posZ = this.z;

        actor.rotationYaw = this.yaw;
        actor.rotationYawHead = this.yawHead;
        actor.rotationPitch = this.pitch;

        actor.motionX = this.motionX;
        actor.motionY = this.motionY;
        actor.motionZ = this.motionZ;

        actor.fallDistance = this.fallDistance;

        actor.isAirBorne = this.isAirBorne;
        actor.setSneaking(this.isSneaking);
        actor.setSprinting(this.isSprinting);
        actor.onGround = this.onGround;
        actor.setElytraFlying(this.flyingElytra);
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