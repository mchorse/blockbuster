package mchorse.blockbuster.network.common.director.sync;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.network.common.director.PacketDirector;
import net.minecraft.util.math.BlockPos;

/**
 * Packet director play
 *
 * This packet stores information about whether to play, stop or pause the
 * director.
 */
public class PacketDirectorPlay extends PacketDirector
{
    public static final byte STOP = 0;
    public static final byte PLAY = 1;
    public static final byte PAUSE = 2;
    public static final byte START = 3;
    public static final byte RESTART = 4;

    public byte state;
    public int tick;

    public PacketDirectorPlay()
    {}

    public PacketDirectorPlay(BlockPos pos, byte state, int tick)
    {
        super(pos);

        this.state = state;
        this.tick = tick;
    }

    public boolean isStop()
    {
        return this.state == STOP;
    }

    public boolean isPlay()
    {
        return this.state == PLAY;
    }

    public boolean isPause()
    {
        return this.state == PAUSE;
    }

    public boolean isStart()
    {
        return this.state == START;
    }

    public boolean isRestart()
    {
        return this.state == RESTART;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);

        buf.writeByte(this.state);
        buf.writeInt(this.tick);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);

        this.state = buf.readByte();
        this.tick = buf.readInt();
    }
}