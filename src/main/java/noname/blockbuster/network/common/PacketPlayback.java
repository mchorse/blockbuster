package noname.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketPlayback implements IMessage
{
    public int mode;
    public String profile;

    public PacketPlayback()
    {}

    public PacketPlayback(int mode, String profile)
    {
        this.mode = mode;
        this.profile = profile;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.mode = buf.readInt();
        this.profile = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.mode);
        ByteBufUtils.writeUTF8String(buf, this.profile);
    }
}
