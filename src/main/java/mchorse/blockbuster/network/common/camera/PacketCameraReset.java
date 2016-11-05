package mchorse.blockbuster.network.common.camera;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketCameraReset implements IMessage
{
    public PacketCameraReset()
    {}

    @Override
    public void fromBytes(ByteBuf buf)
    {}

    @Override
    public void toBytes(ByteBuf buf)
    {}
}