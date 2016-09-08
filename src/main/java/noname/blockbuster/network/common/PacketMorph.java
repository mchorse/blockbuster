package noname.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketMorph implements IMessage
{
    public String model = "";
    public String skin = "";

    public PacketMorph()
    {}

    public PacketMorph(String model, String skin)
    {
        this.model = model;
        this.skin = skin;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.model = ByteBufUtils.readUTF8String(buf);
        this.skin = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.model);
        ByteBufUtils.writeUTF8String(buf, this.skin);
    }
}
