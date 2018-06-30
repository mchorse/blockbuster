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
    public NBTTagCompound itemData;

    public EquipAction()
    {
        this.itemData = new NBTTagCompound();
    }

    public EquipAction(byte armorSlot, ItemStack item)
    {
        this();
        this.armorSlot = armorSlot;

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

        if (this.itemData == null)
        {
            actor.setItemStackToSlot(slot, ItemStack.EMPTY);
        }
        else
        {
            actor.setItemStackToSlot(slot, new ItemStack(this.itemData));
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
        this.itemData = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBuf(ByteBuf buf)
    {
        super.toBuf(buf);

        buf.writeByte(this.armorSlot);
        ByteBufUtils.writeTag(buf, this.itemData);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.armorSlot = tag.getByte("Slot");

        if (tag.hasKey("Data"))
        {
            this.itemData = tag.getCompoundTag("Data");
        }
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        tag.setByte("Slot", this.armorSlot);

        if (this.itemData != null)
        {
            tag.setTag("Data", this.itemData);
        }
    }
}