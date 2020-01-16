package mchorse.blockbuster.aperture.network.common;

import mchorse.blockbuster.network.common.scene.PacketScene;
import net.minecraft.util.math.BlockPos;

public class PacketRequestLength extends PacketScene
{
    public PacketRequestLength()
    {}

    public PacketRequestLength(BlockPos pos)
    {
        super(pos);
    }
}