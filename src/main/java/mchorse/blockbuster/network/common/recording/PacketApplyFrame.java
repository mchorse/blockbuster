package mchorse.blockbuster.network.common.recording;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.recording.data.Frame;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketApplyFrame implements IMessage
{
    private Frame frame;
    private int entityID;

    public PacketApplyFrame()
    { }

    public PacketApplyFrame(Frame frame, int entityID)
    {
        this.frame = frame;
        this.entityID = entityID;
    }

    public Frame getFrame()
    {
        return this.frame.copy();
    }

    public int getEntityID()
    {
        return this.entityID;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        if (buf.readBoolean())
        {
            this.frame = new Frame();

            this.frame.fromBytes(buf);
        }

        this.entityID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(this.frame != null);

        if (this.frame != null)
        {
            this.frame.toBytes(buf);
        }

        buf.writeInt(this.entityID);
    }
}
