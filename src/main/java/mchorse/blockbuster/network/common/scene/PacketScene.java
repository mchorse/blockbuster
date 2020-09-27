package mchorse.blockbuster.network.common.scene;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.blockbuster.recording.scene.SceneLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class PacketScene implements IMessage
{
    public SceneLocation location = new SceneLocation();

    public PacketScene()
    {}

    public PacketScene(SceneLocation location)
    {
        this.location = location;
    }

	public Scene get(World world)
	{
		if (this.location.isScene())
		{
			return CommonProxy.scenes.get(this.location.getFilename(), world);
		}

		return null;
	}

    @Override
    public void fromBytes(ByteBuf buf)
    {
		this.location.fromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
		this.location.toByteBuf(buf);
    }
}