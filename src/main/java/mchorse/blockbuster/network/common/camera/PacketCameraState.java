package mchorse.blockbuster.network.common.camera;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

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
