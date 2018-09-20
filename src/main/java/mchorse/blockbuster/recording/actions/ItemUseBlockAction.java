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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ItemUseBlockAction extends ItemUseAction
{
    public BlockPos pos = BlockPos.ORIGIN;
    public EnumFacing facing = EnumFacing.UP;
    public float hitX;
    public float hitY;
    public float hitZ;

    public ItemUseBlockAction()
    {}

    public ItemUseBlockAction(BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        super(hand);
        this.pos = pos;
        this.facing = facing;
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitZ = hitZ;
    }

    @Override
    public void apply(EntityLivingBase actor)
    {
        ItemStack item = actor.getHeldItem(this.hand);

        if (item != null)
        {
            Frame frame = EntityUtils.getRecordPlayer(actor).getCurrentFrame();
            EntityPlayer player = actor instanceof EntityActor ? ((EntityActor) actor).fakePlayer : (EntityPlayer) actor;

            player.posX = actor.posX;
            player.posY = actor.posY;
            player.posZ = actor.posZ;
            player.rotationYaw = frame.yaw;
            player.rotationPitch = frame.pitch;
            player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, actor.getHeldItemMainhand());
            player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, actor.getHeldItemOffhand());

            ItemStack stack = actor.getHeldItem(this.hand);

            int meta = stack.getMetadata();
            int size = stack.getCount();
            item.getItem().onItemUse(player, actor.world, this.pos, this.hand, this.facing, this.hitX, this.hitY, this.hitZ);
            stack.setItemDamage(meta);
            stack.setCount(size);
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

            this.hitX = (float) vec.x;
            this.hitY = (float) vec.y;
            this.hitZ = (float) vec.z;

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
    public void fromBuf(ByteBuf buf)
    {
        super.fromBuf(buf);

        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.facing = EnumFacing.values()[buf.readByte()];
        this.hitX = buf.readFloat();
        this.hitY = buf.readFloat();
        this.hitZ = buf.readFloat();
    }

    @Override
    public void toBuf(ByteBuf buf)
    {
        super.toBuf(buf);

        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeByte((byte) this.facing.ordinal());
        buf.writeFloat(this.hitX);
        buf.writeFloat(this.hitY);
        buf.writeFloat(this.hitZ);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        this.pos = new BlockPos(tag.getInteger("PosX"), tag.getInteger("PosY"), tag.getInteger("PosZ"));
        this.facing = EnumFacing.values()[tag.getByte("Facing")];
        this.hitX = tag.getFloat("HitX");
        this.hitY = tag.getFloat("HitY");
        this.hitZ = tag.getFloat("HitZ");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        tag.setInteger("PosX", this.pos.getX());
        tag.setInteger("PosY", this.pos.getY());
        tag.setInteger("PosZ", this.pos.getZ());
        tag.setByte("Facing", (byte) this.facing.ordinal());
        tag.setFloat("HitX", this.hitX);
        tag.setFloat("HitY", this.hitX);
        tag.setFloat("HitZ", this.hitX);
    }
}