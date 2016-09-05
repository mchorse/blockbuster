package noname.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketModifyActor implements IMessage
{
    public int id;
    public String filename;
    public String name;
    public String skin;
    public boolean invulnerable;
    public String model;

    public PacketModifyActor()
    {}

    public PacketModifyActor(int id, String filename, String name, String skin, String model, boolean invulnerable)
    {
        this.id = id;
        this.filename = filename;
        this.name = name;
        this.skin = skin;
        this.model = model;
        this.invulnerable = invulnerable;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.filename = ByteBufUtils.readUTF8String(buf);
        this.name = ByteBufUtils.readUTF8String(buf);
        this.skin = ByteBufUtils.readUTF8String(buf);
        this.model = ByteBufUtils.readUTF8String(buf);
        this.invulnerable = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        ByteBufUtils.writeUTF8String(buf, this.filename);
        ByteBufUtils.writeUTF8String(buf, this.name);
        ByteBufUtils.writeUTF8String(buf, this.skin);
        ByteBufUtils.writeUTF8String(buf, this.model);
        buf.writeBoolean(this.invulnerable);
    }
}
