package mchorse.blockbuster.network.common.recording;

import java.util.List;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.recording.data.Frame;

public class PacketFramesChunk extends PacketFrames
{
    public int index;
    public int count;

    public PacketFramesChunk()
    {}

    public PacketFramesChunk(int index, int count, String filename, List<Frame> frames)
    {
        super(filename, frames);

        this.index = index;
        this.count = count;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.index = buf.readInt();
        this.count = buf.readInt();

        super.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.index);
        buf.writeInt(this.count);

        super.toBytes(buf);
    }
}