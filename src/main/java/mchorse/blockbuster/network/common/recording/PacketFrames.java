package mchorse.blockbuster.network.common.recording;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import mchorse.blockbuster.recording.data.Frame;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Packet that responsible for delivering recorded frames either to server for
 * saving or to client for playback.
 */
public abstract class PacketFrames implements IMessage
{
    public String filename;
    public int preDelay;
    public int postDelay;
    public List<Frame> frames;

    public PacketFrames()
    {}

    public PacketFrames(String filename, int preDelay, int postDelay, List<Frame> frames)
    {
        this.filename = filename;
        this.preDelay = preDelay;
        this.postDelay = postDelay;
        this.frames = frames;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        List<Frame> frames = new ArrayList<Frame>();

        this.filename = ByteBufUtils.readUTF8String(buf);
        this.preDelay = buf.readInt();
        this.postDelay = buf.readInt();

        if (buf.readBoolean())
        {
            int count = buf.readInt();

            for (int i = 0; i < count; i++)
            {
                Frame frame = new Frame();

                frame.fromBytes(buf);
                frames.add(frame);
            }

            this.frames = frames;
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.filename);
        buf.writeInt(this.preDelay);
        buf.writeInt(this.postDelay);
        buf.writeBoolean(this.frames != null);

        if (this.frames != null)
        {
            buf.writeInt(this.frames.size());

            for (Frame frame : this.frames)
            {
                frame.toBytes(buf);
            }
        }
    }
}