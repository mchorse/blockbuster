package mchorse.blockbuster.network.common.director;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketDirectorMapAdd extends PacketDirector
{
    public String replay;

    public PacketDirectorMapAdd()
    {}

    public PacketDirectorMapAdd(BlockPos pos, String replay)
    {
        super(pos);
        this.replay = replay;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);
        this.replay = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);
        ByteBufUtils.writeUTF8String(buf, this.replay);
    }
}
