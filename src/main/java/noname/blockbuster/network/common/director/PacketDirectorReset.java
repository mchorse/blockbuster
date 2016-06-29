package noname.blockbuster.network.common.director;

import net.minecraft.util.math.BlockPos;

public class PacketDirectorReset extends PacketDirector
{
    public PacketDirectorReset()
    {}

    public PacketDirectorReset(BlockPos pos)
    {
        super(pos);
    }
}
