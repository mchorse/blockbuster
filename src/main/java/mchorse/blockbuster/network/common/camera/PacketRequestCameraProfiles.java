package mchorse.blockbuster.network.common.camera;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

/**
 * Request server to send list of all camera profiles to the client
 */
public class PacketRequestCameraProfiles implements IMessage
{
    public PacketRequestCameraProfiles()
    {}

    @Override
    public void fromBytes(ByteBuf buf)
    {}

    @Override
    public void toBytes(ByteBuf buf)
    {}
}
