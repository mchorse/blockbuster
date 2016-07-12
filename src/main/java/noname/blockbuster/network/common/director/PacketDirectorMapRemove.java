package noname.blockbuster.network.common.director;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

public class PacketDirectorMapRemove extends PacketDirector
{
    public int id;

    public PacketDirectorMapRemove()
    {}

    public PacketDirectorMapRemove(BlockPos pos, int id)
    {
        super(pos);
        this.id = id;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);
        this.id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);
        buf.writeInt(this.id);
    }
}
