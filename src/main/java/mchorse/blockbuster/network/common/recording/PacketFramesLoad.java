package mchorse.blockbuster.network.common.recording;

import java.util.List;
import java.util.Optional;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.recording.data.Frame;

import javax.annotation.Nullable;

public class PacketFramesLoad extends PacketFrames
{
    private int callbackID = -1;
    private State loaded = State.LOAD;

    public PacketFramesLoad()
    {}

    public PacketFramesLoad(String filename, State loaded, int callbackID)
    {
        this.loaded = loaded;
        this.callbackID = callbackID;
        this.filename = filename;
    }

    public PacketFramesLoad(String filename, State loaded)
    {
        this(filename, loaded, -1);
    }

    public PacketFramesLoad(String filename, int preDelay, int postDelay, List<Frame> frames)
    {
        super(filename, preDelay, postDelay, frames);
    }

    public PacketFramesLoad(String filename, int preDelay, int postDelay, List<Frame> frames, int callbackID)
    {
        super(filename, preDelay, postDelay, frames);

        this.callbackID = callbackID;
    }

    public Optional<Integer> getCallbackID()
    {
        return Optional.of(this.callbackID == -1 ? null : this.callbackID);
    }

    public State getState()
    {
        return this.loaded;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);

        this.loaded = State.values()[buf.readInt()];
        this.callbackID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);

        buf.writeInt(this.loaded.ordinal());
        buf.writeInt(this.callbackID);
    }

    public enum State
    {
        LOAD,
        ERROR,
        NOCHANGES
    }
}