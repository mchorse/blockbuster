package mchorse.blockbuster.recording;

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
}