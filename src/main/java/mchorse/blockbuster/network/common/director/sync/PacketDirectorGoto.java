package mchorse.blockbuster.network.common.director.sync;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.network.common.director.PacketDirector;
import net.minecraft.util.math.BlockPos;

/**
 * Packet director go to
 *
 * This packet stores information about where user wants a director block to go
 * to (in terms of playback ticks).
 */
public class PacketDirectorGoto extends PacketDirector
{
    public int tick;
    public boolean actions;

    public PacketDirectorGoto()
    {}

    public PacketDirectorGoto(BlockPos pos, int tick, boolean actions)
    {
        super(pos);

        this.tick = tick;
        this.actions = actions;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);

        this.tick = buf.readInt();
        this.actions = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);

        buf.writeInt(this.tick);
        buf.writeBoolean(this.actions);
    }
}