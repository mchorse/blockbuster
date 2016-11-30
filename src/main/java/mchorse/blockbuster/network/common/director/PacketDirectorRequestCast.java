package mchorse.blockbuster.network.common.director;

import mchorse.blockbuster.utils.BlockPos;

public class PacketDirectorRequestCast extends PacketDirector
{
    public PacketDirectorRequestCast()
    {}

    public PacketDirectorRequestCast(BlockPos pos)
    {
        super(pos);
    }
}