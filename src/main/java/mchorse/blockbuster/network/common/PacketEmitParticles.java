package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketEmitParticles implements IMessage
{
	public EnumParticleTypes type;
	public double x;
	public double y;
	public double z;
	public int count;
	public double dx;
	public double dy;
	public double dz;
	public double speed;
	public int[] arguments;

	public PacketEmitParticles()
	{}

	public PacketEmitParticles(EnumParticleTypes type, double x, double y, double z, int count, double dx, double dy, double dz, double speed, int[] arguments)
	{
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.count = count;
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
		this.speed = speed;
		this.arguments = arguments;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.type = EnumParticleTypes.values()[buf.readInt()];
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.count = buf.readInt();
		this.dx = buf.readDouble();
		this.dy = buf.readDouble();
		this.dz = buf.readDouble();
		this.speed = buf.readDouble();
		this.arguments = new int[buf.readInt()];

		for (int i = 0; i < this.arguments.length; i ++)
		{
			this.arguments[i] = buf.readInt();
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.type.ordinal());
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeInt(this.count);
		buf.writeDouble(this.dx);
		buf.writeDouble(this.dy);
		buf.writeDouble(this.dz);
		buf.writeDouble(this.speed);
		buf.writeInt(this.arguments.length);

		for (int argument : this.arguments)
		{
			buf.writeInt(argument);
		}
	}
}