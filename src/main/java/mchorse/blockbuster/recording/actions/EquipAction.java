package mchorse.blockbuster.recording.actions;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

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
    public void apply(EntityLivingBase actor)
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
    public void fromBuf(ByteBuf buf)
    {
        super.fromBuf(buf);
        this.armorSlot = buf.readByte();
        this.armorId = buf.readShort();

        if (this.armorId != -1)
        {
            this.itemData = ByteBufUtils.readTag(buf);
        }
    }

    @Override
    public void toBuf(ByteBuf buf)
    {
        super.toBuf(buf);

        buf.writeByte(this.armorSlot);
        buf.writeShort(this.armorId);

        if (this.armorId != -1)
        {
            ByteBufUtils.writeTag(buf, this.itemData);
        }
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.armorSlot = tag.getByte("Slot");
        this.armorId = tag.getShort("Id");

        if (this.armorId != -1)
        {
            this.itemData = tag.getCompoundTag("Data");
        }
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        tag.setByte("Slot", this.armorSlot);
        tag.setShort("Id", this.armorId);

        if (this.armorId != -1)
        {
            tag.setTag("Data", this.itemData);
        }
    }
}