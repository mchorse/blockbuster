package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.network.common.director.PacketDirector;
import net.minecraft.util.math.BlockPos;

public class PacketDirectorDuplicate extends PacketDirector
{
    public int index;

    public PacketDirectorDuplicate()
    {}

    public PacketDirectorDuplicate(BlockPos pos, int index)
    {
        super(pos);
        this.index = index;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);
        this.index = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);
        buf.writeInt(this.index);
    }
}