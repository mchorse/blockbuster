package mchorse.blockbuster.network.common.recording;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.recording.data.Frame;

import java.util.List;

/**
 * Packet of frame ranges, split up in chunks to avoid max packet size error
 */
public class PacketFramesOverwrite extends PacketFrames
{
    /**
     * overwrite frames from tick.
     * This is separate from frame chunk splitting.
     */
    private int from;
    /**
     * overwrite frames to tick.
     * This is separate from frame chunk splitting.
     */
    private int to;
    /**
     * The start index of this frame chunk, relative to {@link #from}
     * This is needed to split it into chunks.
     */
    private int index;

    public PacketFramesOverwrite(int from, int to, int index, String filename, List<Frame> frames)
    {
        super(filename, 0, 0, frames);

        this.from = from;
        this.to = to;
        this.index = index;
    }

    public PacketFramesOverwrite()
    {

    }

    public int getFrom()
    {
        return this.from;
    }

    public void setFrom(int from)
    {
        this.from = from;
    }

    public int getTo()
    {
        return this.to;
    }

    public void setTo(int to)
    {
        this.to = to;
    }

    public int getIndex()
    {
        return this.index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.from = buf.readInt();
        this.to = buf.readInt();
        this.index = buf.readInt();

        super.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.from);
        buf.writeInt(this.to);
        buf.writeInt(this.index);

        super.toBytes(buf);
    }
}
