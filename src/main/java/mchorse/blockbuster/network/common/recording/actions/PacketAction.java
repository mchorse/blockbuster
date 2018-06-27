package mchorse.blockbuster.network.common.recording.actions;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.recording.actions.Action;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketAction implements IMessage
{
    public String filename;
    public int tick = -1;
    public int index = -1;
    public Action action;

    public PacketAction()
    {}

    public PacketAction(String filename, int tick, int index, Action action)
    {
        this.filename = filename;
        this.tick = tick;
        this.index = index;
        this.action = action;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.filename = ByteBufUtils.readUTF8String(buf);
        this.tick = buf.readInt();
        this.index = buf.readInt();

        if (buf.readBoolean())
        {
            try
            {
                this.action = Action.fromType(buf.readByte());
                this.action.fromBuf(buf);
            }
            catch (Exception e)
            {}
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.filename);
        buf.writeInt(this.tick);
        buf.writeInt(this.index);
        buf.writeBoolean(this.action != null);

        if (this.action != null)
        {
            buf.writeByte(this.action.getType());
            this.action.toBuf(buf);
        }
    }
}