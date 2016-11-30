package mchorse.blockbuster.network.common.camera;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class PacketLoadCameraProfile implements IMessage
{
    public String filename;

    public PacketLoadCameraProfile()
    {}

    public PacketLoadCameraProfile(String filename)
    {
        this.filename = filename;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.filename = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.filename);
    }
}
