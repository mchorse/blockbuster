package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketModifyActor implements IMessage
{
    public int id;
    public String model;
    public String skin;
    public boolean invisible;

    public PacketModifyActor()
    {}

    public PacketModifyActor(int id, String model, String skin, boolean invisible)
    {
        this.id = id;
        this.model = model;
        this.skin = skin;
        this.invisible = invisible;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.model = ByteBufUtils.readUTF8String(buf);
        this.skin = ByteBufUtils.readUTF8String(buf);
        this.invisible = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        ByteBufUtils.writeUTF8String(buf, this.model);
        ByteBufUtils.writeUTF8String(buf, this.skin);
        buf.writeBoolean(this.invisible);
    }
}
