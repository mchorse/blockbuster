package mchorse.blockbuster.recording.actions;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
    public EnumHand hand = EnumHand.MAIN_HAND;

    public ItemUseAction()
    {}

    public ItemUseAction(EnumHand hand)
    {
        this.hand = hand;
    }

    @Override
    public void apply(EntityLivingBase actor)
    {
        ItemStack item = actor.getHeldItem(this.hand);

        if (item != null)
        {
            Frame frame = EntityUtils.getRecordPlayer(actor).getCurrentFrame();
            EntityPlayer player = actor instanceof EntityActor ? ((EntityActor) actor).fakePlayer : (EntityPlayer) actor;

            if (frame == null) return;

            player.width = actor.width;
            player.height = actor.height;
            player.eyeHeight = actor.getEyeHeight();
            player.setEntityBoundingBox(actor.getEntityBoundingBox());

            player.posX = actor.posX;
            player.posY = actor.posY;
            player.posZ = actor.posZ;
            player.rotationYaw = frame.yaw;
            player.rotationPitch = frame.pitch;
            player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, actor.getHeldItemMainhand());
            player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, actor.getHeldItemOffhand());

            item.getItem().onItemRightClick(actor.world, player, this.hand);
        }
    }

    @Override
    public void fromBuf(ByteBuf buf)
    {
        super.fromBuf(buf);
        this.hand = buf.readByte() == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
    }

    @Override
    public void toBuf(ByteBuf buf)
    {
        super.toBuf(buf);
        buf.writeByte((byte) (this.hand.equals(EnumHand.MAIN_HAND) ? 0 : 1));
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