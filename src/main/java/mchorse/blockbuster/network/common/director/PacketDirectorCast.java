package mchorse.blockbuster.network.common.director;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.recording.director.Director;
import net.minecraft.util.math.BlockPos;

public class PacketDirectorCast extends PacketDirector
{
    public Director director = new Director(null);

    public PacketDirectorCast()
    {}

    public PacketDirectorCast(BlockPos pos, Director director)
    {
        super(pos);
        this.director = director;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);

        this.director.fromBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);

        this.director.toBuf(buf);
    }
}