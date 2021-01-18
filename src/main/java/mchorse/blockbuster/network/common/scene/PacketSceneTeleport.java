package mchorse.blockbuster.network.common.scene;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSceneTeleport implements IMessage
{
	public String id = "";
	public int offset;

	public PacketSceneTeleport()
	{}

	public PacketSceneTeleport(String id, int offset)
	{
		this(id);

		this.offset = offset;
	}

	public PacketSceneTeleport(String id)
	{
		this.id = id;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.id = ByteBufUtils.readUTF8String(buf);
		this.offset = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, this.id);
		buf.writeInt(this.offset);
	}
}