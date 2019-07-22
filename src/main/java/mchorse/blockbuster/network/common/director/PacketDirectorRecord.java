package mchorse.blockbuster.network.common.director;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketDirectorRecord implements IMessage
{
    public BlockPos pos = BlockPos.ORIGIN;
    public String record = "";

    public PacketDirectorRecord()
    {}

    public PacketDirectorRecord(BlockPos pos, String record)
    {
        this.pos = pos;
        this.record = record;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.record = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        ByteBufUtils.writeUTF8String(buf, this.record);
    }
}