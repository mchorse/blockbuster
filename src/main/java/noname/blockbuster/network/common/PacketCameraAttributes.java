package noname.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketCameraAttributes implements IMessage
{
    public int id;
    public String name;
    public float speed;
    public float accelerationRate;
    public float accelerationMax;
    public boolean canFly;

    public PacketCameraAttributes()
    {}

    public PacketCameraAttributes(int eid, String name, float speeed, float rate, float max, boolean fly)
    {
        this.id = eid;
        this.name = name;
        this.speed = speeed;
        this.accelerationRate = rate;
        this.accelerationMax = max;
        this.canFly = fly;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.name = ByteBufUtils.readUTF8String(buf);
        this.speed = buf.readFloat();
        this.accelerationRate = buf.readFloat();
        this.accelerationMax = buf.readFloat();
        this.canFly = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        ByteBufUtils.writeUTF8String(buf, this.name);
        buf.writeFloat(this.speed);
        buf.writeFloat(this.accelerationRate);
        buf.writeFloat(this.accelerationMax);
        buf.writeBoolean(this.canFly);
    }
}