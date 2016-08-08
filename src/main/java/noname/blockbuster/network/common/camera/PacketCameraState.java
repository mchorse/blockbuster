package noname.blockbuster.network.common.camera;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketCameraState implements IMessage
{
    public boolean toPlay;

    public PacketCameraState()
    {}

    public PacketCameraState(boolean toPlay)
    {
        this.toPlay = toPlay;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.toPlay = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(this.toPlay);
    }
}
