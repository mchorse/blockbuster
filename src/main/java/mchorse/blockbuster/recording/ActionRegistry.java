package mchorse.blockbuster.recording;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.AttackAction;
import mchorse.blockbuster.recording.actions.BreakBlockAction;
import mchorse.blockbuster.recording.actions.BreakBlockAnimation;
import mchorse.blockbuster.recording.actions.ChatAction;
import mchorse.blockbuster.recording.actions.CommandAction;
import mchorse.blockbuster.recording.actions.DamageAction;
import mchorse.blockbuster.recording.actions.DropAction;
import mchorse.blockbuster.recording.actions.EquipAction;
import mchorse.blockbuster.recording.actions.InteractBlockAction;
import mchorse.blockbuster.recording.actions.ItemUseAction;
import mchorse.blockbuster.recording.actions.ItemUseBlockAction;
import mchorse.blockbuster.recording.actions.MorphAction;
import mchorse.blockbuster.recording.actions.MorphActionAction;
import mchorse.blockbuster.recording.actions.MountingAction;
import mchorse.blockbuster.recording.actions.PlaceBlockAction;
import mchorse.blockbuster.recording.actions.ShootArrowAction;
import mchorse.blockbuster.recording.actions.SwipeAction;

public class ActionRegistry
{
    /**
     * Bi-directional map between class and byte ID
     */
    public static final BiMap<Class<? extends Action>, Byte> CLASS_TO_ID = HashBiMap.create();

    /**
     * Bi-directional map  map of action types mapped to corresponding class
     */
    public static final BiMap<String, Class<? extends Action>> NAME_TO_CLASS = HashBiMap.create();

    /**
     * A mapping between string named to byte type of the fixture
     */
    public static final Map<String, Byte> NAME_TO_ID = new HashMap<String, Byte>();

    /**
     * Next available id 
     */
    private static byte NEXT_ID = 0;

    /**
     * Create an action from type
     */
    public static Action fromType(byte type) throws Exception
    {
        Class<? extends Action> clazz = CLASS_TO_ID.inverse().get(type);

        if (clazz == null)
        {
            throw new Exception("Action by type '" + type + "' wasn't found!");
        }

        return clazz.getConstructor().newInstance();
    }

    /**
     * Create an action from type
     */
    public static Action fromName(String name) throws Exception
    {
        Class<? extends Action> clazz = NAME_TO_CLASS.get(name);

        if (clazz == null)
        {
            throw new Exception("Action by type '" + name + "' wasn't found!");
        }

        return clazz.getConstructor().newInstance();
    }

    /**
     * Get type of the action 
     */
    public static byte getType(Action action)
    {
        Byte type = CLASS_TO_ID.get(action.getClass());

        return type == null ? -1 : type;
    }

    /**
     * Write an action to byte buffer 
     */
    public static void toByteBuf(Action action, ByteBuf buffer)
    {
        byte type = CLASS_TO_ID.get(action.getClass());

        buffer.writeByte(type);
        action.toBuf(buffer);
    }

    /**
     * Create an action out of byte buffer
     */
    public static Action fromByteBuf(ByteBuf buffer)
    {
        byte type = buffer.readByte();

        try
        {
            Action action = fromType(type);

            action.fromBuf(buffer);

            return action;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Register given camera fixture
     */
    public static void register(String name, Class<? extends Action> clazz)
    {
        register(name, NEXT_ID, clazz);

        NEXT_ID++;
    }

    /**
     * Register given camera fixture
     */
    private static void register(String name, int id, Class<? extends Action> clazz)
    {
        if (CLASS_TO_ID.containsKey(clazz))
        {
            return;
        }

        CLASS_TO_ID.put(clazz, (byte) id);
        NAME_TO_ID.put(name, (byte) id);
        NAME_TO_CLASS.put(name, clazz);
    }

    static
    {
        /* Register Blockbuster actions */
        register("chat", 1, ChatAction.class);
        register("swipe", 2, SwipeAction.class);
        register("drop", 3, DropAction.class);
        register("equip", 4, EquipAction.class);
        register("shoot_arrow", 5, ShootArrowAction.class);
        register("place_block", 7, PlaceBlockAction.class);
        register("mounting", 8, MountingAction.class);
        register("interact_block", 9, InteractBlockAction.class);
        register("break_block", 10, BreakBlockAction.class);
        register("morph", 12, MorphAction.class);
        register("attack", 13, AttackAction.class);
        register("damage", 14, DamageAction.class);
        register("morph_action", 15, MorphActionAction.class);
        register("command", 16, CommandAction.class);
        register("break_animation", 17, BreakBlockAnimation.class);
        register("use_item", 18, ItemUseAction.class);
        register("use_item_block", 19, ItemUseBlockAction.class);

        /* Set next ID to max */
        NEXT_ID = 20;
    }
}