package mchorse.blockbuster.aperture.network.common;

import mchorse.blockbuster.network.common.director.PacketDirector;
import net.minecraft.util.math.BlockPos;

public class PacketRequestLength extends PacketDirector
{
    public PacketRequestLength()
    {}

    public PacketRequestLength(BlockPos pos)
    {
        super(pos);
    }
}