package noname.blockbuster.network.common;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import noname.blockbuster.camera.CameraProfile;

public class PacketUpdateProfile implements IMessage
{
    public CameraProfile profile;

    public PacketUpdateProfile()
    {}

    public PacketUpdateProfile(CameraProfile profile)
    {
        this.profile = profile;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.profile = new CameraProfile();

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
