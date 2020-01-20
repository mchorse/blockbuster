package mchorse.blockbuster.network.common.scene;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.recording.scene.SceneLocation;

public class PacketSceneCast extends PacketScene
{
    public boolean open = true;

    public PacketSceneCast()
    {}

    public PacketSceneCast(SceneLocation location)
    {
        super(location);
    }

    public PacketSceneCast open(boolean open)
    {
        this.open = open;

        return this;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);

        this.open = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);

        buf.writeBoolean(this.open);
    }
}