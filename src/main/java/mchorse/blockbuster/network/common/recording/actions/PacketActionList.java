package mchorse.blockbuster.network.common.recording.actions;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketActionList implements IMessage
{
    public List<String> records = new ArrayList<String>();

    public PacketActionList()
    {}

    public PacketActionList(List<String> records)
    {
        this.records.addAll(records);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        for (int i = 0, c = buf.readInt(); i < c; i++)
        {
            this.records.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.records.size());

        for (String record : this.records)
        {
            ByteBufUtils.writeUTF8String(buf, record);
        }
    }
}