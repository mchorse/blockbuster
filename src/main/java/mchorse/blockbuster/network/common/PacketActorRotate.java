package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketActorRotate implements IMessage
{
    public int id;
    public float yaw;
    public float pitch;

    public PacketActorRotate()
    {}

    public PacketActorRotate(int id, float yaw, float pitch)
    {
        this.id = id;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
    }
}