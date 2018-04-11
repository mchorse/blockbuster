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

    public float yaw;
    public float pitch;
    public float body;

    public float x;
    public float y;
    public float z;

    public float rx;
    public float ry;
    public float rz;

    public float sx;
    public float sy;
    public float sz;

    public PacketModifyModelBlock()
    {}

    public PacketModifyModelBlock(BlockPos pos, AbstractMorph morph)
    {
        this.pos = pos;
        this.morph = morph;
    }

    public PacketModifyModelBlock setBody(float x, float y, float z)
    {
        this.yaw = x;
        this.pitch = y;
        this.body = z;

        return this;
    }

    public PacketModifyModelBlock setPos(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;

        return this;
    }

    public PacketModifyModelBlock setRot(float x, float y, float z)
    {
        this.rx = x;
        this.ry = y;
        this.rz = z;

        return this;
    }

    public PacketModifyModelBlock setScale(float x, float y, float z)
    {
        this.sx = x;
        this.sy = y;
        this.sz = z;

        return this;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());

        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
        this.body = buf.readFloat();

        this.x = buf.readFloat();
        this.y = buf.readFloat();
        this.z = buf.readFloat();
        this.rx = buf.readFloat();
        this.ry = buf.readFloat();
        this.rz = buf.readFloat();
        this.sx = buf.readFloat();
        this.sy = buf.readFloat();
        this.sz = buf.readFloat();

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

        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
        buf.writeFloat(this.body);

        buf.writeFloat(this.x);
        buf.writeFloat(this.y);
        buf.writeFloat(this.z);
        buf.writeFloat(this.rx);
        buf.writeFloat(this.ry);
        buf.writeFloat(this.rz);
        buf.writeFloat(this.sx);
        buf.writeFloat(this.sy);
        buf.writeFloat(this.sz);
        buf.writeBoolean(this.morph != null);

        if (this.morph != null)
        {
            NBTTagCompound tag = new NBTTagCompound();

            this.morph.toNBT(tag);
            ByteBufUtils.writeTag(buf, tag);
        }
    }
}