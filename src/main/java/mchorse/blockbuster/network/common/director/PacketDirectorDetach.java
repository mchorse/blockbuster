package mchorse.blockbuster.network.common.director;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

public class PacketDirectorDetach extends PacketDirector
{
    public int index;

    public PacketDirectorDetach()
    {}

    public PacketDirectorDetach(BlockPos pos, int index)
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