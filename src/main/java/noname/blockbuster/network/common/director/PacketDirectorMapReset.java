package noname.blockbuster.network.common.director;

import net.minecraft.util.math.BlockPos;

public class PacketDirectorMapReset extends PacketDirector
{
    public PacketDirectorMapReset()
    {}

    public PacketDirectorMapReset(BlockPos pos)
    {
        super(pos);
    }
}
