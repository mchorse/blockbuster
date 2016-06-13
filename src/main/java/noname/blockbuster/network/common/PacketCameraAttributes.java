package noname.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketCameraAttributes implements IMessage
{
    public int id;
    public float speed;
    public float accelerationRate;
    public float accelerationMax;
    public boolean canFly;

    public PacketCameraAttributes()
    {}

    public PacketCameraAttributes(int eid, float speeed, float rate, float max, boolean fly)
    {
        this.id = eid;
        this.speed = speeed;
        this.accelerationRate = rate;
        this.accelerationMax = max;
        this.canFly = fly;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.speed = buf.readFloat();
        this.accelerationRate = buf.readFloat();
        this.accelerationMax = buf.readFloat();
        this.canFly = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        buf.writeFloat(this.speed);
        buf.writeFloat(this.accelerationRate);
        buf.writeFloat(this.accelerationMax);
        buf.writeBoolean(this.canFly);
    }
}