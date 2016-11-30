package mchorse.blockbuster.network.common.director;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.utils.BlockPos;

public class PacketDirectorAdd extends PacketDirector
{
    public String id;

    public PacketDirectorAdd()
    {}

    public PacketDirectorAdd(BlockPos pos, String id)
    {
        super(pos);
        this.id = id;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);

        this.id = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);

        ByteBufUtils.writeUTF8String(buf, this.id);
    }
}