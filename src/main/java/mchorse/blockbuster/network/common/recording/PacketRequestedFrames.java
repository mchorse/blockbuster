package mchorse.blockbuster.network.common.recording;

import java.util.List;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.recording.data.Frame;

public class PacketRequestedFrames extends PacketFrames
{
    public int id;

    public PacketRequestedFrames()
    {}

    public PacketRequestedFrames(int id, String filename, int preDelay, int postDelay, List<Frame> frames)
    {
        super(filename, preDelay, postDelay, frames);

        this.id = id;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);

        this.id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);

        buf.writeInt(this.id);
    }
}