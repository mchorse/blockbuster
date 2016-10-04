package mchorse.blockbuster.network.common.recording;

import java.util.List;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.recording.data.Frame;

public class PacketFramesLoad extends PacketFrames
{
    public int id;

    public PacketFramesLoad()
    {}

    public PacketFramesLoad(int id, String filename, List<Frame> frames)
    {
        super(filename, frames);

        this.id = id;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);

        super.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        this.id = buf.readInt();

        super.toBytes(buf);
    }
}