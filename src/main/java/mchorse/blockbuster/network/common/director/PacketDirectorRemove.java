package mchorse.blockbuster.network.common.director;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.utils.BlockPos;

public class PacketDirectorRemove extends PacketDirector
{
    public int id;

    public PacketDirectorRemove()
    {}

    public PacketDirectorRemove(BlockPos pos, int id)
    {
        super(pos);
        this.id = id;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);
        this.id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);
        buf.writeInt(this.id);
    }
}