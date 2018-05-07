package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketReloadModels implements IMessage
{
    public boolean force;

    public PacketReloadModels()
    {}

    public PacketReloadModels(boolean force)
    {
        this.force = force;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.force = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(this.force);
    }
}