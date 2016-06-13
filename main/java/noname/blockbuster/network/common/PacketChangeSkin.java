package noname.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketChangeSkin implements IMessage
{
    public int id;
    public String skin;

    public PacketChangeSkin()
    {}

    public PacketChangeSkin(int id, String skin)
    {
        this.id = id;
        this.skin = skin;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.skin = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        ByteBufUtils.writeUTF8String(buf, this.skin);
    }
}
