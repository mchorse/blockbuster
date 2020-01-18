package mchorse.blockbuster.network.common.scene;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.recording.scene.Director;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.blockbuster.recording.scene.SceneLocation;

public class PacketSceneCast extends PacketScene
{
    public Scene scene;
    public boolean open = true;

    public PacketSceneCast()
    {}

    public PacketSceneCast(SceneLocation location, Scene scene)
    {
        super(location);
        this.scene = scene;
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

        this.scene = this.location.isDirector() ? new Director(null) : new Scene();
        this.scene.fromBuf(buf);
        this.open = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);

        this.scene.toBuf(buf);
        buf.writeBoolean(this.open);
    }
}