package noname.blockbuster.network.common.camera;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import noname.blockbuster.camera.CameraProfile;

public class PacketCameraProfile implements IMessage
{
    public boolean play;
    public String filename;
    public CameraProfile profile;

    public PacketCameraProfile()
    {}

    public PacketCameraProfile(String filename, CameraProfile profile)
    {
        this(filename, profile, false);
    }

    public PacketCameraProfile(String filename, CameraProfile profile, boolean play)
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
        this.profile = new CameraProfile(this.filename);

        try
        {
            this.profile.read(new ByteBufInputStream(buf));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(this.play);
        ByteBufUtils.writeUTF8String(buf, this.filename);

        try
        {
            this.profile.write(new ByteBufOutputStream(buf));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
