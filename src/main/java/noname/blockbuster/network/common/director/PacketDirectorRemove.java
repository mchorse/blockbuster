package noname.blockbuster.network.common.director;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

public class PacketDirectorRemove extends PacketDirector
{
    public int id;
    public boolean type;

    public PacketDirectorRemove()
    {}

    public PacketDirectorRemove(BlockPos pos, int id, boolean type)
    {
        super(pos);
        this.id = id;
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);
        this.id = buf.readInt();
        this.type = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);
        buf.writeInt(this.id);
        buf.writeBoolean(this.type);
    }
}