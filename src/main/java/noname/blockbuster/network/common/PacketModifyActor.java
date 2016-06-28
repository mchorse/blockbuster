package noname.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketModifyActor implements IMessage
{
    public int id;
    public boolean invulnerable;
    public String name;
    public String skin;

    public PacketModifyActor()
    {}

    public PacketModifyActor(int id, boolean invulnerable, String name, String skin)
    {
        this.id = id;
        this.invulnerable = invulnerable;
        this.name = name;
        this.skin = skin;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.invulnerable = buf.readBoolean();
        this.name = ByteBufUtils.readUTF8String(buf);
        this.skin = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        buf.writeBoolean(this.invulnerable);
        ByteBufUtils.writeUTF8String(buf, this.name);
        ByteBufUtils.writeUTF8String(buf, this.skin);
    }
}
