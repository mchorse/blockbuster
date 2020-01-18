package mchorse.blockbuster.network.common.scene.sync;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.network.common.scene.PacketScene;
import mchorse.blockbuster.recording.scene.SceneLocation;

/**
 * Packet director play
 *
 * This packet stores information about whether to play, stop or pause the
 * director.
 */
public class PacketScenePlay extends PacketScene
{
    public static final byte STOP = 0;
    public static final byte PLAY = 1;
    public static final byte PAUSE = 2;
    public static final byte START = 3;
    public static final byte RESTART = 4;

    public byte state;
    public int tick;

    public PacketScenePlay()
    {}

    public PacketScenePlay(SceneLocation location, byte state, int tick)
    {
        super(location);

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