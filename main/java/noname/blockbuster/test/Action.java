package noname.blockbuster.test;

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

	public Action(byte chat)
	{
		this.type = chat;
		this.itemData = new NBTTagCompound();
	}
}
