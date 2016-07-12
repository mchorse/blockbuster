package noname.blockbuster.network.common.director;

import net.minecraft.util.math.BlockPos;

public class PacketDirectorRequestCast extends PacketDirector
{
    public PacketDirectorRequestCast()
    {}

    public PacketDirectorRequestCast(BlockPos pos)
    {
        super(pos);
    }
}
