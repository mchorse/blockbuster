package mchorse.blockbuster.network.common.director;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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

    public static void replaysFromBytes(ByteBuf buf, List<String> list)
    {
        int count = buf.readInt();

        for (int i = 0; i < count; i++)
        {
            list.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    public static void replaysToBytes(ByteBuf buf, List<String> list)
    {
        buf.writeInt(list.size());

        for (String string : list)
        {
            ByteBufUtils.writeUTF8String(buf, string);
        }
    }
}