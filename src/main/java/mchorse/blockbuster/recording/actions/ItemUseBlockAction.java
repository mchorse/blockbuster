package mchorse.blockbuster.recording.actions;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.data.Frame;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ItemUseBlockAction extends Action
{
    public BlockPos pos;
    public EnumHand hand;
    public EnumFacing facing;
    public float hitX;
    public float hitY;
    public float hitZ;

    public ItemUseBlockAction()
    {}

    public ItemUseBlockAction(BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        this.pos = pos;
        this.hand = hand;
        this.facing = facing;
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitZ = hitZ;
    }

    @Override
    public byte getType()
    {
        return Action.USE_ITEM_BLOCK;
    }

    @Override
    public void apply(EntityActor actor)
    {
        ItemStack item = actor.getHeldItem(this.hand);

        if (item != null)
        {
            Frame frame = actor.playback.getCurrentFrame();

            actor.fakePlayer.posX = actor.posX;
            actor.fakePlayer.posY = actor.posY;
            actor.fakePlayer.posZ = actor.posZ;
            actor.fakePlayer.rotationYaw = frame.yaw;
            actor.fakePlayer.rotationYawHead = frame.yawHead;
            actor.fakePlayer.rotationPitch = frame.pitch;
            actor.fakePlayer.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, actor.getHeldItemMainhand());
            actor.fakePlayer.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, actor.getHeldItemOffhand());

            item.getItem().onItemUse(actor.fakePlayer, actor.world, this.pos, this.hand, this.facing, this.hitX, this.hitY, this.hitZ);
        }
    }

    @Override
    public void changeOrigin(double rotation, double newX, double newY, double newZ, double firstX, double firstY, double firstZ)
    {
        /* I don't like wasting variables */
        firstX = this.pos.getX() - firstX;
        firstX = this.pos.getY() - firstY;
        firstX = this.pos.getZ() - firstZ;

        if (rotation != 0)
        {
            Vec3d vec = new Vec3d(this.hitX, this.hitY, this.hitZ);

            vec = vec.rotateYaw((float) (rotation / 180 * Math.PI));

            this.hitX = (float) vec.xCoord;
            this.hitY = (float) vec.yCoord;
            this.hitZ = (float) vec.zCoord;

            float cos = (float) Math.cos(rotation / 180 * Math.PI);
            float sin = (float) Math.sin(rotation / 180 * Math.PI);

            double xx = firstX * cos - firstZ * sin;
            double zz = firstX * sin + firstZ * cos;

            firstX = xx;
            firstZ = zz;
        }

        newX += firstX;
        newY += firstY;
        newZ += firstZ;

        this.pos = new BlockPos(newX, newY, newZ);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.pos = new BlockPos(tag.getInteger("PosX"), tag.getInteger("PosY"), tag.getInteger("PosZ"));
        this.hand = tag.getByte("Hand") == 1 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        this.facing = EnumFacing.values()[tag.getByte("Facing")];
        this.hitX = tag.getFloat("HitX");
        this.hitY = tag.getFloat("HitY");
        this.hitZ = tag.getFloat("HitZ");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        tag.setInteger("PosX", this.pos.getX());
        tag.setInteger("PosY", this.pos.getY());
        tag.setInteger("PosZ", this.pos.getZ());
        tag.setByte("Hand", this.hand == EnumHand.MAIN_HAND ? (byte) 0 : (byte) 1);
        tag.setByte("Facing", (byte) this.facing.ordinal());
        tag.setFloat("HitX", this.hitX);
        tag.setFloat("HitY", this.hitX);
        tag.setFloat("HitZ", this.hitX);
    }
}