package mchorse.blockbuster.network.common.recording;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketPlayerRecording implements IMessage
{
    public boolean recording;
    public String filename;

    public PacketPlayerRecording()
    {}

    public PacketPlayerRecording(boolean recording, String filename)
    {
        this.recording = recording;
        this.filename = filename;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.recording = buf.readBoolean();
        this.filename = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(this.recording);
        ByteBufUtils.writeUTF8String(buf, this.filename);
    }
}