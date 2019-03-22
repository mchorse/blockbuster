package mchorse.blockbuster.network.common.structure;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketStructureRequest implements IMessage
{
    public String name = "";
    public long modified;

    public PacketStructureRequest()
    {}

    public PacketStructureRequest(String name, long modified)
    {
        this.name = name;
        this.modified = modified;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.name = ByteBufUtils.readUTF8String(buf);
        this.modified = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.name);
        buf.writeLong(this.modified);
    }
}