package mchorse.blockbuster.network.common.director;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.common.tileentity.director.Replay;
import net.minecraft.util.math.BlockPos;

public class PacketDirectorCast extends PacketDirector
{
    public List<Replay> actors = new ArrayList<Replay>();

    public PacketDirectorCast()
    {}

    public PacketDirectorCast(BlockPos pos, List<Replay> actors)
    {
        super(pos);
        this.actors.addAll(actors);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);

        this.replaysFromBytes(buf, this.actors);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);

        this.replaysToBytes(buf, this.actors);
    }

    public void replaysFromBytes(ByteBuf buf, List<Replay> list)
    {
        int count = buf.readInt();

        for (int i = 0; i < count; i++)
        {
            Replay replay = new Replay();

            replay.fromBuf(buf);
            list.add(replay);
        }
    }

    public void replaysToBytes(ByteBuf buf, List<Replay> list)
    {
        buf.writeInt(list.size());

        for (Replay replay : list)
        {
            replay.toBuf(buf);
        }
    }
}
