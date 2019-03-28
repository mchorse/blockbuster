package mchorse.blockbuster.network.common.structure;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketStructureRequest implements IMessage
{
    public String name = "";

    public PacketStructureRequest()
    {}

    public PacketStructureRequest(String name)
    {
        this.name = name;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.name = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.name);
    }
}