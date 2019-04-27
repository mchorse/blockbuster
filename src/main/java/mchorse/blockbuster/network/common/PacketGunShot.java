package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketGunShot implements IMessage
{
    public int entity;

    public PacketGunShot()
    {}

    public PacketGunShot(int entity)
    {
        this.entity = entity;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entity = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.entity);
    }
}