package mchorse.blockbuster.recording.data;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.common.entity.EntityActor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * Recording frame class
 *
 * This class stores state data about the player in the specific frame that was
 * captured.
 */
public class Frame
{
    public static DataParameter<Byte> FLAGS;

    /* Position */
    public double x;
    public double y;
    public double z;

    /* Rotation */
    public float yaw;
    public float yawHead;
    public float pitch;

    public boolean hasBodyYaw;
    public float bodyYaw;

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

    /* Client data */
    public float roll;

    /* Active hand */
    public int activeHands;

    private int hotbarSlot;

    /* Methods for retrieving/applying state data */

    /**
     * Set frame fields from player entity.
     */
    public void fromPlayer(EntityPlayer player)
    {
        Entity mount = player.isRiding() ? player.getRidingEntity() : player;

        /* Position and rotation */
        this.x = mount.posX;
        this.y = player.isRiding() && player.getRidingEntity().posY > player.posY ? player.posY : mount.posY;
        this.z = mount.posZ;

        this.yaw = player.rotationYaw;
        this.yawHead = player.rotationYawHead;
        this.pitch = player.rotationPitch;

        this.hasBodyYaw = true;
        this.bodyYaw = player.renderYawOffset;

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

        if (player.world.isRemote)
        {
            this.fromPlayerClient(player);
        }

        this.hotbarSlot = player.inventory.currentItem;
    }

    @SideOnly(Side.CLIENT)
    private void fromPlayerClient(EntityPlayer player)
    {
        EntityPlayerSP local = Minecraft.getMinecraft().player;

        if (player == local)
        {
            this.roll = CameraHandler.getRoll();
        }
    }

    /**
     * Apply frame properties on actor. Different actions will be made
     * depending on which side this method was invoked.
     *
     * Use second argument to force things to be cool.
     */
    public void apply(EntityLivingBase actor, boolean force)
    {
        boolean isRemote = actor.world.isRemote;

        Entity mount = actor.isRiding() ? actor.getRidingEntity() : actor;

        if (mount instanceof EntityActor)
        {
            mount = actor;
        }

        if (actor instanceof EntityActor)
        {
            EntityActor theActor = (EntityActor) actor;

            theActor.isMounted = this.isMounted;
            theActor.roll = this.roll;
        }

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

                if (actor == mount)
                {
                    actor.setPosition(this.x, this.y, this.z);
                }
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

            this.setFlag(actor, 7, this.flyingElytra);
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

        if (actor instanceof EntityPlayer)
        {
            ((EntityPlayer) actor).inventory.currentItem = this.hotbarSlot;
        }
    }

    /**
     * Set entity flags... if only vanilla could expose that shit 
     */
    private void setFlag(EntityLivingBase actor, int i, boolean flag)
    {
        if (FLAGS == null)
        {
            Field field = null;

            for (Field f : Entity.class.getDeclaredFields())
            {
                int mod = f.getModifiers();
                Type type = f.getGenericType();

                if (Modifier.isProtected(mod) && Modifier.isStatic(mod) && Modifier.isFinal(mod) && f.getType() == DataParameter.class)
                {
                    field = f;
                    break;
                }
            }

            if (field != null)
            {
                try
                {
                    field.setAccessible(true);
                    FLAGS = (DataParameter<Byte>) field.get(null);
                }
                catch (Exception e)
                {}
            }
        }

        if (FLAGS != null)
        {
            byte flags = actor.getDataManager().get(FLAGS).byteValue();

            actor.getDataManager().set(FLAGS, (byte) (flag ? flags | (1 << i) : flags & ~(1 << i)));
        }
    }

    /**
     * Create a copy of this frame 
     */
    public Frame copy()
    {
        Frame frame = new Frame();

        frame.x = this.x;
        frame.y = this.y;
        frame.z = this.z;

        frame.yaw = this.yaw;
        frame.yawHead = this.yawHead;
        frame.pitch = this.pitch;

        frame.hasBodyYaw = this.hasBodyYaw;
        frame.bodyYaw = this.bodyYaw;

        frame.isMounted = this.isMounted;

        if (frame.isMounted)
        {
            frame.mountYaw = this.mountYaw;
            frame.mountPitch = this.mountPitch;
        }

        frame.motionX = this.motionX;
        frame.motionY = this.motionY;
        frame.motionZ = this.motionZ;

        frame.fallDistance = this.fallDistance;

        frame.isAirBorne = this.isAirBorne;
        frame.isSneaking = this.isSneaking;
        frame.isSprinting = this.isSprinting;
        frame.onGround = this.onGround;
        frame.flyingElytra = this.flyingElytra;

        frame.activeHands = this.activeHands;

        frame.roll = this.roll;

        frame.hotbarSlot = this.hotbarSlot;

        return frame;
    }

    /* Save/load frame instance */
    public void toBytes(ByteBuf buf)
    {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);

        buf.writeFloat(this.yaw);
        buf.writeFloat(this.yawHead);
        buf.writeFloat(this.pitch);

        buf.writeBoolean(this.hasBodyYaw);

        if (this.hasBodyYaw)
        {
            buf.writeFloat(this.bodyYaw);
        }

        buf.writeBoolean(this.isMounted);

        if (this.isMounted)
        {
            buf.writeFloat(this.mountYaw);
            buf.writeFloat(this.mountPitch);
        }

        buf.writeFloat((float) this.motionX);
        buf.writeFloat((float) this.motionY);
        buf.writeFloat((float) this.motionZ);

        buf.writeFloat(this.fallDistance);

        buf.writeBoolean(this.isAirBorne);
        buf.writeBoolean(this.isSneaking);
        buf.writeBoolean(this.isSprinting);
        buf.writeBoolean(this.onGround);
        buf.writeBoolean(this.flyingElytra);

        buf.writeByte(this.activeHands);

        buf.writeFloat(this.roll);

        buf.writeInt(this.hotbarSlot);
    }

    public void fromBytes(ByteBuf buf)
    {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();

        this.yaw = buf.readFloat();
        this.yawHead = buf.readFloat();
        this.pitch = buf.readFloat();

        if (buf.readBoolean())
        {
            this.hasBodyYaw = true;
            this.bodyYaw = buf.readFloat();
        }

        this.isMounted = buf.readBoolean();

        if (this.isMounted)
        {
            this.mountYaw = buf.readFloat();
            this.mountPitch = buf.readFloat();
        }

        this.motionX = buf.readFloat();
        this.motionY = buf.readFloat();
        this.motionZ = buf.readFloat();

        this.fallDistance = buf.readFloat();

        this.isAirBorne = buf.readBoolean();
        this.isSneaking = buf.readBoolean();
        this.isSprinting = buf.readBoolean();
        this.onGround = buf.readBoolean();
        this.flyingElytra = buf.readBoolean();

        this.activeHands = buf.readByte();

        this.roll = buf.readFloat();
        this.hotbarSlot = buf.readInt();
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
        tag.setDouble("X", this.x);
        tag.setDouble("Y", this.y);
        tag.setDouble("Z", this.z);

        tag.setFloat("MX", (float) this.motionX);
        tag.setFloat("MY", (float) this.motionX);
        tag.setFloat("MZ", (float) this.motionX);

        tag.setFloat("RX", this.yaw);
        tag.setFloat("RY", this.pitch);
        tag.setFloat("RZ", this.yawHead);

        if (this.hasBodyYaw)
        {
            tag.setFloat("RW", this.bodyYaw);
        }

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

        if (this.roll != 0)
        {
            tag.setFloat("Roll", this.roll);
        }

        tag.setInteger("HotbarSlot", this.hotbarSlot);
    }

    /**
     * Read frame data from NBT tag. Used for loading frame from disk.
     *
     * This is going to be extracted in the future to support compatibility.
     */
    public void fromNBT(NBTTagCompound tag)
    {
        this.x = tag.getDouble("X");
        this.y = tag.getDouble("Y");
        this.z = tag.getDouble("Z");

        this.motionX = tag.getFloat("MX");
        this.motionY = tag.getFloat("MY");
        this.motionZ = tag.getFloat("MZ");

        this.yaw = tag.getFloat("RX");
        this.pitch = tag.getFloat("RY");
        this.yawHead = tag.getFloat("RZ");

        if (tag.hasKey("RW"))
        {
            this.hasBodyYaw = true;
            this.bodyYaw = tag.getFloat("RW");
        }

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

        if (tag.hasKey("Roll"))
        {
            this.roll = tag.getFloat("Roll");
        }

        this.hotbarSlot = tag.getInteger("HotbarSlot");
    }

    public enum RotationChannel
    {
        HEAD_YAW,
        HEAD_PITCH,
        BODY_YAW
    }
}