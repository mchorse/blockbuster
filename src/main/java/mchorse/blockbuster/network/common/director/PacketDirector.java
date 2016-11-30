package mchorse.blockbuster.network.common.director;

import java.util.List;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.utils.BlockPos;

public abstract class PacketDirector implements IMessage
{
    public BlockPos pos;

    public PacketDirector()
    {}

    public PacketDirector(BlockPos pos)
    {
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
    }

    public static void listFromBytes(ByteBuf buf, List<String> list)
    {
        int count = buf.readInt();

        for (int i = 0; i < count; i++)
        {
            list.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    public static void listToBytes(ByteBuf buf, List<String> list)
    {
        buf.writeInt(list.size());

        for (String string : list)
        {
            ByteBufUtils.writeUTF8String(buf, string);
        }
    }
}