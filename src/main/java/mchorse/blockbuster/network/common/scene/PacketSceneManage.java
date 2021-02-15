package mchorse.blockbuster.network.common.scene;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSceneManage implements IMessage
{
    public static final int RENAME = 1;
    public static final int REMOVE = 2;

    public String source;
    public String destination;
    public int action;

    public PacketSceneManage()
    {}

    public PacketSceneManage(String source, String destination, int action)
    {
        this.source = source;
        this.destination = destination;
        this.action = action;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.source = ByteBufUtils.readUTF8String(buf);
        this.destination = ByteBufUtils.readUTF8String(buf);
        this.action = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.source);
        ByteBufUtils.writeUTF8String(buf, this.destination);
        buf.writeInt(this.action);
    }
}