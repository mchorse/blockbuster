package mchorse.blockbuster.network.common.camera;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketCameraProfile implements IMessage
{
    public boolean play;
    public String filename;
    public String profile;

    public PacketCameraProfile()
    {}

    public PacketCameraProfile(String filename, String profile)
    {
        this(filename, profile, false);
    }

    public PacketCameraProfile(String filename, String profile, boolean play)
    {
        this.play = play;
        this.filename = filename;
        this.profile = profile;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.play = buf.readBoolean();
        this.filename = ByteBufUtils.readUTF8String(buf);
        this.profile = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(this.play);
        ByteBufUtils.writeUTF8String(buf, this.filename);
        ByteBufUtils.writeUTF8String(buf, this.profile);
    }
}