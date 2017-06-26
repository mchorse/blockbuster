package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketActorPause implements IMessage
{
    public int id;
    public boolean pause;
    public int tick;

    public PacketActorPause()
    {}

    public PacketActorPause(int id, boolean pause, int tick)
    {
        this.id = id;
        this.pause = pause;
        this.tick = tick;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.pause = buf.readBoolean();
        this.tick = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        buf.writeBoolean(this.pause);
        buf.writeInt(this.tick);
    }
}