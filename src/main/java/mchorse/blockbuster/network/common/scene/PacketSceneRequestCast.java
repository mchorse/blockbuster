package mchorse.blockbuster.network.common.scene;

import net.minecraft.util.math.BlockPos;

public class PacketSceneRequestCast extends PacketScene
{
    public PacketSceneRequestCast()
    {}

    public PacketSceneRequestCast(BlockPos pos)
    {
        super(pos);
    }

	public PacketSceneRequestCast(String filename)
	{
		super(filename);
	}
}
