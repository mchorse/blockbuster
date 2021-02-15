package mchorse.blockbuster.aperture.network.common;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.network.common.scene.PacketScene;
import mchorse.blockbuster.recording.scene.SceneLocation;

public class PacketAudioShift extends PacketScene
{
    public int shift;

    public PacketAudioShift()
    {
        super();
    }

    public PacketAudioShift(SceneLocation location, int shift)
    {
        super(location);

        this.shift = shift;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);

        this.shift = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);

        buf.writeInt(this.shift);
    }
}