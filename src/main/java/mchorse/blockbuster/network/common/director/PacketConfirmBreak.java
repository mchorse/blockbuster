package mchorse.blockbuster.network.common.director;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

public class PacketConfirmBreak extends PacketDirector
{
    public int count;

    public PacketConfirmBreak()
    {}

    public PacketConfirmBreak(BlockPos pos, int count)
    {
        this.pos = pos;
        this.count = count;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);
        this.count = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);
        buf.writeInt(this.count);
    }
}