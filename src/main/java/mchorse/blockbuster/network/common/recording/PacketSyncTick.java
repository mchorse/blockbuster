package mchorse.blockbuster.network.common.recording;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class PacketSyncTick implements IMessage
{
    public int id;
    public int tick;

    public PacketSyncTick()
    {}

    public PacketSyncTick(int id, int tick)
    {
        this.id = id;
        this.tick = tick;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.tick = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        buf.writeInt(this.tick);
    }
}