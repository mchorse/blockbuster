package noname.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSwitchCamera implements IMessage
{
    public int direction;

    public PacketSwitchCamera()
    {}

    public PacketSwitchCamera(int direction)
    {
        this.direction = direction;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.direction = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.direction);
    }
}
