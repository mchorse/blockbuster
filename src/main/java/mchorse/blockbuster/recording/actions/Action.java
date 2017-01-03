package mchorse.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import mchorse.blockbuster.common.entity.EntityActor;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Parent of all recording actions
 *
 * This class holds additional information about player's actions performed during
 * recording. Supports abstraction and stuffz.
 */
public abstract class Action
{
    /* Action types */
    public static final byte CHAT = 1;
    public static final byte SWIPE = 2;
    public static final byte DROP = 3;
    public static final byte EQUIP = 4;
    public static final byte SHOOT_ARROW = 5;
    /* public static final byte LOGOUT = 6; */
    public static final byte PLACE_BLOCK = 7;
    public static final byte MOUNTING = 8;
    public static final byte INTERACT_BLOCK = 9;
    public static final byte BREAK_BLOCK = 10;
    /* public static final byte ELYTRA_FLYING = 11; */
    public static final byte MORPH = 12;
    public static final byte ATTACK = 13;

    /**
     * Factory method
     *
     * Creates an action class from given type
     */
    public static Action fromType(byte type) throws Exception
    {
        if (type == CHAT) return new ChatAction();
        if (type == SWIPE) return new SwipeAction();
        if (type == DROP) return new DropAction();
        if (type == EQUIP) return new EquipAction();
        if (type == SHOOT_ARROW) return new ShootArrowAction();
        if (type == PLACE_BLOCK) return new PlaceBlockAction();
        if (type == MOUNTING) return new MountingAction();
        if (type == INTERACT_BLOCK) return new InteractBlockAction();
        if (type == BREAK_BLOCK) return new BreakBlockAction();
        if (type == MORPH) return new MorphAction();
        if (type == ATTACK) return new AttackAction();

        throw new Exception("Action by type '" + type + "' doesn't exist!");
    }

    /**
     * Get type of the action
     */
    public abstract byte getType();

    /**
     * Apply action on an actor (shoot arrow, mount entity, break block, etc.)
     *
     * Some action doesn't necessarily should have apply method (that's why this
     * method is empty)
     */
    public void apply(EntityActor actor)
    {}

    /**
     * Construct action from data input stream. Used to send over the network.
     */
    public void fromBytes(DataInput in) throws IOException
    {}

    /**
     * Persist action to data output stream. Used to send over the network.
     */
    public void toBytes(DataOutput out) throws IOException
    {}

    /**
     * Persist action from NBT tag. Used for loading from the disk.
     */
    public void fromNBT(NBTTagCompound tag)
    {}

    /**
     * Persist action to NBT tag. Used for saving to the disk.
     */
    public void toNBT(NBTTagCompound tag)
    {}
}