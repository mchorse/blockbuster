package noname.blockbuster.network.common.director;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

public class PacketDirectorCast extends PacketDirector
{
    public List<String> actors = new ArrayList<String>();

    public PacketDirectorCast()
    {}

    public PacketDirectorCast(BlockPos pos, List<String> actors)
    {
        super(pos);
        this.actors.addAll(actors);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);

        PacketDirector.listFromBytes(buf, this.actors);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);

        PacketDirector.listToBytes(buf, this.actors);
    }
}
