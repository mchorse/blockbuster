package mchorse.blockbuster.network.common.scene;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSceneTeleport implements IMessage
{
	public String id = "";

	public PacketSceneTeleport()
	{}

	public PacketSceneTeleport(String id)
	{
		this.id = id;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.id = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, this.id);
	}
}