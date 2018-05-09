package mchorse.blockbuster.recording.actions;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.data.Frame;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

/**
 * Item use action
 *
 * This action is responsible for using the item in actor's hand. This action
 * will right click the item, not will use it on the block.
 */
public class ItemUseAction extends Action
{
    public EnumHand hand;

    public ItemUseAction()
    {}

    public ItemUseAction(EnumHand hand)
    {
        this.hand = hand;
    }

    @Override
    public byte getType()
    {
        return Action.USE_ITEM;
    }

    @Override
    public void apply(EntityActor actor)
    {
        ItemStack item = actor.getHeldItem(this.hand);

        if (item != null)
        {
            Frame frame = actor.playback.record.frames.get(actor.playback.tick);

            actor.fakePlayer.posX = actor.posX;
            actor.fakePlayer.posY = actor.posY;
            actor.fakePlayer.posZ = actor.posZ;
            actor.fakePlayer.rotationYaw = frame.yaw;
            actor.fakePlayer.rotationYawHead = frame.yawHead;
            actor.fakePlayer.rotationPitch = frame.pitch;
            actor.fakePlayer.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, actor.getHeldItemMainhand());
            actor.fakePlayer.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, actor.getHeldItemOffhand());

            item.getItem().onItemRightClick(item, actor.worldObj, actor.fakePlayer, this.hand);
        }
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.hand = tag.getByte("Hand") == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        tag.setByte("Hand", (byte) (this.hand.equals(EnumHand.MAIN_HAND) ? 0 : 1));
    }
}