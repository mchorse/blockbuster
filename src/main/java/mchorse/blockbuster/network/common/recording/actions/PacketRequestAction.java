package mchorse.blockbuster.network.common.recording.actions;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketRequestAction implements IMessage
{
    public String filename;
    public boolean open;

    public PacketRequestAction()
    {}

    public PacketRequestAction(String filename, boolean open)
    {
        this.filename = filename;
        this.open = open;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.filename = ByteBufUtils.readUTF8String(buf);
        this.open = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.filename);
        buf.writeBoolean(this.open);
    }
}