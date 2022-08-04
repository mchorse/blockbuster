package mchorse.blockbuster.network.common.recording;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.recording.scene.Replay;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketPlayback implements IMessage
{
    public int id;
    public boolean state;
    public boolean realPlayer;
    public String filename;
    public Replay replay;

    public PacketPlayback()
    {}

    public PacketPlayback(int id, boolean state, boolean realPlayer, String filename)
    {
        this.id = id;
        this.state = state;
        this.filename = filename;
        this.realPlayer = realPlayer;
    }

    public PacketPlayback(int id, boolean state, boolean realPlayer, String filename, Replay replay)
    {
        this(id, state, realPlayer, filename);

        this.replay = replay;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.state = buf.readBoolean();
        this.realPlayer = buf.readBoolean();
        this.filename = ByteBufUtils.readUTF8String(buf);

        if (buf.readBoolean())
        {
            this.replay = new Replay();

            this.replay.fromBuf(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        buf.writeBoolean(this.state);
        buf.writeBoolean(this.realPlayer);
        ByteBufUtils.writeUTF8String(buf, this.filename);
        buf.writeBoolean(this.replay != null);

        if (this.replay != null)
        {
            this.replay.toBuf(buf);
        }
    }
}
