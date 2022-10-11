package mchorse.blockbuster.network.common.recording;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nullable;
import java.util.Optional;

public class PacketRequestRecording implements IMessage
{
    private String filename = "";
    private int callbackID = -1;

    public PacketRequestRecording()
    {}

    public PacketRequestRecording(String record)
    {
        this(record, -1);
    }

    public PacketRequestRecording(String record, int callbackID)
    {
        this.filename = record;
        this.callbackID = callbackID;
    }

    public String getFilename()
    {
        return this.filename;
    }

    public Optional<Integer> getCallbackID()
    {
        return Optional.of(this.callbackID == -1 ? null : this.callbackID);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.filename = ByteBufUtils.readUTF8String(buf);
        this.callbackID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.filename);
        buf.writeInt(this.callbackID);
    }
}