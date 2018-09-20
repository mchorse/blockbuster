package mchorse.blockbuster.network.common.recording;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketUpdatePlayerData implements IMessage
{
    public String record = "";

    public PacketUpdatePlayerData()
    {}

    public PacketUpdatePlayerData(String record)
    {
        this.record = record;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.record = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.record);
    }
}