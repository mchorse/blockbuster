package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextComponent.Serializer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketCaption implements IMessage
{
    public ITextComponent caption;

    public PacketCaption()
    {}

    public PacketCaption(ITextComponent caption)
    {
        this.caption = caption;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        if (buf.readBoolean())
        {
            this.caption = Serializer.jsonToComponent(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(this.caption != null);

        if (this.caption != null)
        {
            ByteBufUtils.writeUTF8String(buf, Serializer.componentToJson(this.caption));
        }
    }
}