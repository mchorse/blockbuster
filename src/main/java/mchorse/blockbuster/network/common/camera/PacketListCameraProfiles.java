package mchorse.blockbuster.network.common.camera;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.network.common.director.PacketDirector;

public class PacketListCameraProfiles implements IMessage
{
    public List<String> profiles = new ArrayList<String>();

    public PacketListCameraProfiles()
    {}

    public PacketListCameraProfiles(List<String> profiles)
    {
        this.profiles.addAll(profiles);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        PacketDirector.listFromBytes(buf, this.profiles);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        PacketDirector.listToBytes(buf, this.profiles);
    }
}
