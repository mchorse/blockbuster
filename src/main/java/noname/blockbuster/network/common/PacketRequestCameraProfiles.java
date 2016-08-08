package noname.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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
