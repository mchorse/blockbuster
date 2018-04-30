package mchorse.blockbuster.recording.actions;

import java.util.HashMap;
import java.util.Map;

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
    public static Map<String, Integer> TYPES = new HashMap<String, Integer>();

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
    public static final byte DAMAGE = 14;
    public static final byte MORPH_ACTION = 15;
    public static final byte COMMAND = 16;
    public static final byte BREAK_ANIMATION = 17;
    public static final byte USE_ITEM = 18;
    public static final byte USE_ITEM_BLOCK = 19;

    /* Register actions */
    static
    {
        TYPES.put("chat", 1);
        TYPES.put("swipe", 2);
        TYPES.put("drop", 3);
        TYPES.put("equip", 4);
        TYPES.put("shoot_arrow", 5);
        TYPES.put("place_block", 7);
        TYPES.put("mounting", 8);
        TYPES.put("interact_block", 9);
        TYPES.put("break_block", 10);
        TYPES.put("morph", 12);
        TYPES.put("attack", 13);
        TYPES.put("damage", 14);
        TYPES.put("morph_action", 15);
        TYPES.put("command", 16);
        TYPES.put("break_animation", 17);
        TYPES.put("use_item", 18);
        TYPES.put("use_item_block", 19);
    }

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
        if (type == DAMAGE) return new DamageAction();
        if (type == MORPH_ACTION) return new MorphActionAction();
        if (type == COMMAND) return new CommandAction();
        if (type == BREAK_ANIMATION) return new BreakBlockAnimation();
        if (type == USE_ITEM) return new ItemUseAction();
        if (type == USE_ITEM_BLOCK) return new ItemUseBlockAction();

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

    public void changeOrigin(double rotation, double newX, double newY, double newZ, double firstX, double firstY, double firstZ)
    {}

    /* TODO: Action method which were responsible for writing and reading data
     * from network were removed, but they will come back in 1.5 update.
     */

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