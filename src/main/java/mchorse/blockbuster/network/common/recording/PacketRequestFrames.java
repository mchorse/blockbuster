package mchorse.blockbuster.network.common.recording;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class PacketRequestFrames implements IMessage
{
    public int id;
    public String filename;

    public PacketRequestFrames()
    {}

    public PacketRequestFrames(int id, String filename)
    {
        this.id = id;
        this.filename = filename;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.filename = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        ByteBufUtils.writeUTF8String(buf, this.filename);
    }
}