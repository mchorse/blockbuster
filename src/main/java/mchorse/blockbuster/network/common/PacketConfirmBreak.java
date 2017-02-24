package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketConfirmBreak implements IMessage
{
    public BlockPos pos;
    public int count;

    public PacketConfirmBreak()
    {}

    public PacketConfirmBreak(BlockPos pos, int count)
    {
        this.pos = pos;
        this.count = count;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.count = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeInt(this.count);
    }
}