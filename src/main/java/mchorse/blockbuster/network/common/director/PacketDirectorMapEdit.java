package mchorse.blockbuster.network.common.director;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketDirectorMapEdit extends PacketDirector
{
    public int id;
    public String replay;

    public PacketDirectorMapEdit()
    {}

    public PacketDirectorMapEdit(BlockPos pos, int id, String replay)
    {
        super(pos);
        this.id = id;
        this.replay = replay;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);
        this.id = buf.readInt();
        this.replay = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);
        buf.writeInt(this.id);
        ByteBufUtils.writeUTF8String(buf, this.replay);
    }
}
