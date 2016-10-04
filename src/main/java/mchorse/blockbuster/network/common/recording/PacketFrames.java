package mchorse.blockbuster.network.common.recording;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import mchorse.blockbuster.recording.data.Frame;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Packet that responsible for delivering recorded frames either to server
 * or to client for playback or saving.
 */
public abstract class PacketFrames implements IMessage
{
    public String filename;
    public List<Frame> frames;

    public PacketFrames()
    {}

    public PacketFrames(String filename, List<Frame> frames)
    {
        this.filename = filename;
        this.frames = frames;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        ByteBufInputStream input = new ByteBufInputStream(buf);
        List<Frame> frames = new ArrayList<Frame>();

        try
        {
            this.filename = input.readUTF();
            int count = input.readInt();

            for (int i = 0; i < count; i++)
            {
                Frame frame = new Frame();

                frame.fromBytes(input);
                frames.add(frame);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufOutputStream output = new ByteBufOutputStream(buf);

        try
        {
            output.writeUTF(this.filename);
            output.writeInt(this.frames.size());

            for (Frame frame : this.frames)
            {
                frame.toBytes(output);
            }

            output.close();
        }
        catch (IOException e)
        {
            System.out.println("Couldn't convert frames to ByteBuf!");
            e.printStackTrace();
        }
    }
}