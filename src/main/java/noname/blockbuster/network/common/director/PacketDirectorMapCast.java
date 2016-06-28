package noname.blockbuster.network.common.director;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketDirectorMapCast extends PacketDirector
{
    public List<String> cast = new ArrayList<String>();

    public PacketDirectorMapCast()
    {}

    public PacketDirectorMapCast(List<String> cast, BlockPos pos)
    {
        super(pos);
        this.cast.addAll(cast);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);

        int count = buf.readInt();

        for (int i = 0; i < count; i++)
        {
            this.cast.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);
        buf.writeInt(this.cast.size());

        for (String member : this.cast)
        {
            ByteBufUtils.writeUTF8String(buf, member);
        }
    }
}
