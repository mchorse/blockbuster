package mchorse.blockbuster.recording.actions;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
    private byte hotbarSlot = -1;

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

    public EquipAction(byte armorSlot, byte hotbarSlot, ItemStack item)
    {
        this(armorSlot, item);

        this.hotbarSlot = hotbarSlot;
    }

    @Override
    public void apply(EntityLivingBase actor)
    {
        EntityEquipmentSlot slot = this.getSlotByIndex(this.armorSlot);

        if (slot == null)
        {
            return;
        }

        if (this.itemData == null)
        {
            this.updateCurrentItemIndex(actor, slot);

            actor.setItemStackToSlot(slot, ItemStack.EMPTY);
        }
        else
        {
            this.updateCurrentItemIndex(actor, slot);

            actor.setItemStackToSlot(slot, new ItemStack(this.itemData));
        }
    }

    /**
     * The currentItem index in the inventory can be delayed (client shows different current slot than what is on server).
     * The method {@link EntityLivingBase#setItemStackToSlot(EntityEquipmentSlot, ItemStack)}
     * sets the currentItem index to the provided itemStack for MAINHAND, this can screw up the inventory.
     */
    private void updateCurrentItemIndex(EntityLivingBase entity, EntityEquipmentSlot slot)
    {
        if (entity instanceof EntityPlayer && this.hotbarSlot != -1 && slot == EntityEquipmentSlot.MAINHAND)
        {
            ((EntityPlayer) entity).inventory.currentItem = this.hotbarSlot;
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
        this.hotbarSlot = buf.readByte();
        this.itemData = NBTUtils.readInfiniteTag(buf);
    }

    @Override
    public void toBuf(ByteBuf buf)
    {
        super.toBuf(buf);

        buf.writeByte(this.armorSlot);
        buf.writeByte(this.hotbarSlot);
        ByteBufUtils.writeTag(buf, this.itemData);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.armorSlot = tag.getByte("Slot");
        this.hotbarSlot = tag.hasKey("HotbarSlot") ? tag.getByte("HotbarSlot") : this.hotbarSlot;

        if (tag.hasKey("Data"))
        {
            this.itemData = tag.getCompoundTag("Data");
        }
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        tag.setByte("Slot", this.armorSlot);

        if (this.hotbarSlot != -1)
        {
            tag.setByte("HotbarSlot", this.hotbarSlot);
        }

        if (this.itemData != null)
        {
            tag.setTag("Data", this.itemData);
        }
    }

    @Override
    public boolean isSafe()
    {
        return true;
    }
}