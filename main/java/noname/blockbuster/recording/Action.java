package noname.blockbuster.recording;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

public class Action
{
    public byte type;
    public String message;
    public int armorId;
    public int armorSlot;
    public int armorDmg;
    public NBTTagCompound itemData;
    public int arrowCharge;
    public int xCoord;
    public int yCoord;
    public int zCoord;
    public UUID target;

    public Action(byte type)
    {
        this.type = type;
        this.itemData = new NBTTagCompound();
    }

    /*
     * Total rip-off from Mocap mod
     *
     * I probably need to ask author's permission to use these values (and code)
     * for my mod... Nah, I think it's fine.
     */
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
}
