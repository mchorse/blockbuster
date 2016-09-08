package mchorse.blockbuster.network.common.director;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

public class PacketDirectorMapCast extends PacketDirector
{
    public List<String> cast = new ArrayList<String>();

    public PacketDirectorMapCast()
    {}

    public PacketDirectorMapCast(List<String> cast, BlockPos pos)
    {
        super(pos);
        this.cast.addAll(cast);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);

        PacketDirector.listFromBytes(buf, this.cast);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);

        PacketDirector.listToBytes(buf, this.cast);
    }
}
