package mchorse.blockbuster.recording.actions;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class HotbarChangeAction extends Action
{
    private int slotToChange = -1;
    private NBTTagCompound newItemStack;

    public HotbarChangeAction()
    {
        this.newItemStack = new NBTTagCompound();
    }

    public HotbarChangeAction(int slotToChange, ItemStack newItemStack)
    {
        this();
        this.slotToChange = slotToChange;

        if (newItemStack != null)
        {
            this.newItemStack = newItemStack.writeToNBT(new NBTTagCompound());
        }
    }

    public int getSlot()
    {
        return this.slotToChange;
    }

    public void setSlot(int slot)
    {
        this.slotToChange = slot;
    }

    public ItemStack getItemStack()
    {
        return new ItemStack(this.newItemStack);
    }

    public void setItemStack(ItemStack itemStack)
    {
        if (itemStack != null)
        {
            this.newItemStack = itemStack.writeToNBT(new NBTTagCompound());
        }
    }

    @Override
    public void apply(EntityLivingBase entity)
    {
        if (entity instanceof EntityPlayer && this.slotToChange != -1)
        {
            EntityPlayer player = (EntityPlayer) entity;

            player.inventory.setInventorySlotContents(this.slotToChange, new ItemStack(this.newItemStack));
        }
    }

    @Override
    public void fromBuf(ByteBuf buf)
    {
        super.fromBuf(buf);
        this.slotToChange = buf.readInt();
        this.newItemStack = NBTUtils.readInfiniteTag(buf);
    }

    @Override
    public void toBuf(ByteBuf buf)
    {
        super.toBuf(buf);

        buf.writeInt(this.slotToChange);
        ByteBufUtils.writeTag(buf, this.newItemStack);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.slotToChange = tag.hasKey("Slot") ? tag.getInteger("Slot") : this.slotToChange;

        if (tag.hasKey("ItemStack"))
        {
            this.newItemStack = tag.getCompoundTag("ItemStack");
        }
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        if (this.slotToChange != -1)
        {
            tag.setInteger("Slot", this.slotToChange);
        }

        if (this.newItemStack != null)
        {
            tag.setTag("ItemStack", this.newItemStack);
        }
    }

    @Override
    public boolean isSafe()
    {
        return true;
    }
}
