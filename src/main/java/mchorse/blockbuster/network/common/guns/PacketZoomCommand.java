package mchorse.blockbuster.network.common.guns;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class PacketZoomCommand implements IMessage
{
    public int entity;
    public boolean zoomOn;

    public PacketZoomCommand()
    {}

    public PacketZoomCommand(int entity, boolean zoomOn)
    {
        super();
        this.entity = entity;
        this.zoomOn = zoomOn;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf)
    {
        this.entity = byteBuf.readInt();
        this.zoomOn = byteBuf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf byteBuf)
    {
        byteBuf.writeInt(this.entity);
        byteBuf.writeBoolean(this.zoomOn);
    }
}