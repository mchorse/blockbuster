package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketCaption implements IMessage
{
    public String caption = "";

    public PacketCaption()
    {}

    public PacketCaption(String caption)
    {
        this.caption = caption;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.caption = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.caption);
    }
}