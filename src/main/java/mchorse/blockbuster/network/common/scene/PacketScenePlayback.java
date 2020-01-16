package mchorse.blockbuster.network.common.scene;

import net.minecraft.util.math.BlockPos;

public class PacketScenePlayback extends PacketScene
{
    public PacketScenePlayback()
    {}

    public PacketScenePlayback(BlockPos pos)
    {
        super(pos);
    }

    public PacketScenePlayback(String filename)
    {
        super(filename);
    }
}