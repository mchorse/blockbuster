package mchorse.blockbuster.aperture.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSceneLength implements IMessage
{
    public int length;
    public int shift;

    public PacketSceneLength()
    {}

    public PacketSceneLength(int length, int shift)
    {
        this.length = length;
        this.shift = shift;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.length = buf.readInt();
        this.shift = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.length);
        buf.writeInt(this.shift);
    }
}