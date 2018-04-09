package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketModifyModelBlock implements IMessage
{
    public BlockPos pos;
    public AbstractMorph morph;
    public float rotateX;
    public float rotateY;
    public float x;
    public float y;
    public float z;

    public PacketModifyModelBlock()
    {}

    public PacketModifyModelBlock(BlockPos pos, AbstractMorph morph, float rotateX, float rotateY, float x, float y, float z)
    {
        this.pos = pos;
        this.morph = morph;
        this.rotateX = rotateX;
        this.rotateY = rotateY;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.rotateX = buf.readFloat();
        this.rotateY = buf.readFloat();
        this.x = buf.readFloat();
        this.y = buf.readFloat();
        this.z = buf.readFloat();

        if (buf.readBoolean())
        {
            this.morph = MorphManager.INSTANCE.morphFromNBT(ByteBufUtils.readTag(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeFloat(this.rotateX);
        buf.writeFloat(this.rotateY);
        buf.writeFloat(this.x);
        buf.writeFloat(this.y);
        buf.writeFloat(this.z);
        buf.writeBoolean(this.morph != null);

        if (this.morph != null)
        {
            NBTTagCompound tag = new NBTTagCompound();

            this.morph.toNBT(tag);
            ByteBufUtils.writeTag(buf, tag);
        }
    }
}