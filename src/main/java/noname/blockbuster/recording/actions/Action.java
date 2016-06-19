package noname.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import noname.blockbuster.entity.ActorEntity;

/**
 * Parent of all recording actions
 *
 * This class holds additional information about player's actions performed during
 * recording. Supports abstraction and stuffz.
 */
public class Action
{
    /* Enums from Mocap mod. */
    public static final byte CHAT = 1;
    public static final byte SWIPE = 2;
    public static final byte DROP = 3;
    public static final byte EQUIP = 4;
    public static final byte SHOOTARROW = 5;
    public static final byte LOGOUT = 6;
    public static final byte PLACE_BLOCK = 7;

    /* These types of handling are added by me */
    public static final byte MOUNTING = 8;
    public static final byte INTERACT_BLOCK = 9;
    public static final byte BREAK_BLOCK = 10;

    /**
     * Factory method
     *
     * Creates an action class from given type
     */
    public static Action fromType(byte type) throws Exception
    {
        Action action = null;

        if (type == CHAT)
            action = new ChatAction();
        if (type == SWIPE)
            action = new SwipeAction();
        if (type == DROP)
            action = new DropAction();
        if (type == EQUIP)
            action = new EquipAction();
        if (type == LOGOUT)
            action = new LogoutAction();
        if (type == PLACE_BLOCK)
            action = new PlaceBlockAction();
        if (type == MOUNTING)
            action = new MountingAction();
        if (type == INTERACT_BLOCK)
            action = new InteractBlockAction();
        if (type == BREAK_BLOCK)
            action = new BreakBlockAction();

        if (action != null)
        {
            return action;
        }

        throw new Exception("Action by type '" + type + "' doesn't exist!");
    }

    public byte type;

    public Action(byte type)
    {
        this.type = type;
    }

    public void apply(ActorEntity actor)
    {}

    public void fromBytes(DataInput in) throws IOException
    {}

    public void toBytes(DataOutput out) throws IOException
    {}
}
