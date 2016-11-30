package mchorse.blockbuster.network.common.director;

import mchorse.blockbuster.utils.BlockPos;

public class PacketDirectorReset extends PacketDirector
{
    public PacketDirectorReset()
    {}

    public PacketDirectorReset(BlockPos pos)
    {
        super(pos);
    }
}
