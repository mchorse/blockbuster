package mchorse.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

import mchorse.blockbuster.common.entity.EntityActor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Equip item action
 *
 * This action equips an item from replay to the actor, so he either equips the
 * item into one of the hands or in of the armor slots (shoes, leggins, chestplate,
 * or helmet)
 *
 * This action is also called to "de-equip" an item from equipment
 */
public class EquipAction extends Action
{
    public byte armorSlot;
    public short armorId;
    public NBTTagCompound itemData;

    public EquipAction()
    {
        this.itemData = new NBTTagCompound();
    }

    public EquipAction(byte armorSlot, short armorId, ItemStack item)
    {
        this();
        this.armorSlot = armorSlot;
        this.armorId = armorId;

        if (item != null)
        {
            item.writeToNBT(this.itemData);
        }
    }

    @Override
    public byte getType()
    {
        return Action.EQUIP;
    }

    @Override
    public void apply(EntityActor actor)
    {
        EntityEquipmentSlot slot = this.getSlotByIndex(this.armorSlot);

        if (this.armorId == -1)
        {
            actor.setItemStackToSlot(slot, null);
        }
        else
        {
            actor.setItemStackToSlot(slot, ItemStack.loadItemStackFromNBT(this.itemData));
        }
    }

    private EntityEquipmentSlot getSlotByIndex(int index)
    {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values())
        {
            if (slot.getSlotIndex() == index) return slot;
        }

        return null;
    }

    @Override
    public void fromBytes(DataInput in) throws IOException
    {
        this.armorSlot = in.readByte();
        this.armorId = in.readShort();

        if (this.armorId != -1)
        {
            this.itemData = CompressedStreamTools.read((DataInputStream) in);
        }
    }

    @Override
    public void toBytes(DataOutput out) throws IOException
    {
        out.writeByte(this.armorSlot);
        out.writeShort(this.armorId);

        if (this.armorId != -1)
        {
            CompressedStreamTools.write(this.itemData, out);
        }
    }
}
