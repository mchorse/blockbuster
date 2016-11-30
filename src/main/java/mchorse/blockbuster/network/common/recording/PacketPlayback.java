package mchorse.blockbuster.network.common.recording;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class PacketPlayback implements IMessage
{
    public int id;
    public boolean state;
    public String filename;

    public PacketPlayback()
    {}

    public PacketPlayback(int id, boolean state, String filename)
    {
        this.id = id;
        this.state = state;
        this.filename = filename;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.state = buf.readBoolean();
        this.filename = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        buf.writeBoolean(this.state);
        ByteBufUtils.writeUTF8String(buf, this.filename);
    }
}
