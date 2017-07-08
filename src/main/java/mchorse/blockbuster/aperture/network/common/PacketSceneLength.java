package mchorse.blockbuster.aperture.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSceneLength implements IMessage
{
    public int length;

    public PacketSceneLength()
    {}

    public PacketSceneLength(int length)
    {
        this.length = length;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.length = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.length);
    }
}