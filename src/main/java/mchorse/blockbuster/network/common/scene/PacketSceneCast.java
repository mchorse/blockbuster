package mchorse.blockbuster.network.common.scene;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.recording.director.Director;
import mchorse.blockbuster.recording.director.Scene;
import net.minecraft.util.math.BlockPos;

public class PacketSceneCast extends PacketScene
{
    public Scene scene;

    public PacketSceneCast()
    {}

    public PacketSceneCast(BlockPos pos, Director director)
    {
        super(pos);
        this.scene = director;
    }

    public PacketSceneCast(String filename, Scene scene)
    {
        super(filename);
        this.scene = scene;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);

        this.scene = this.isDirector() ? new Director(null) : new Scene();
        this.scene.fromBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);

        this.scene.toBuf(buf);
    }
}