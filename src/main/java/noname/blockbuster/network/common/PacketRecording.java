package noname.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketRecording implements IMessage
{
    public int id;
    public boolean recording;

    public PacketRecording()
    {}

    public PacketRecording(int id, boolean recording)
    {
        this.id = id;
        this.recording = recording;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.recording = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        buf.writeBoolean(this.recording);
    }
}
