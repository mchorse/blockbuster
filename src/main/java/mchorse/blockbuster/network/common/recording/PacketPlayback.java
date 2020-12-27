package mchorse.blockbuster.network.common.recording;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketPlayback implements IMessage
{
    public int id;
    public boolean state;
    public boolean realPlayer;
    public String filename;

    public PacketPlayback()
    {}

    public PacketPlayback(int id, boolean state, boolean realPlayer, String filename)
    {
        this.id = id;
        this.state = state;
        this.filename = filename;
        this.realPlayer = realPlayer;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.state = buf.readBoolean();
        this.realPlayer = buf.readBoolean();
        this.filename = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        buf.writeBoolean(this.state);
        buf.writeBoolean(this.realPlayer);
        ByteBufUtils.writeUTF8String(buf, this.filename);
    }
}
