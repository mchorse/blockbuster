package mchorse.blockbuster.network.common.scene.sync;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.network.common.scene.PacketScene;
import net.minecraft.util.math.BlockPos;

/**
 * Packet director go to
 *
 * This packet stores information about where user wants a director block to go
 * to (in terms of playback ticks).
 */
public class PacketSceneGoto extends PacketScene
{
    public int tick;
    public boolean actions;

    public PacketSceneGoto()
    {}

    public PacketSceneGoto(BlockPos pos, int tick, boolean actions)
    {
        super(pos);

        this.tick = tick;
        this.actions = actions;
    }

    public PacketSceneGoto(String filename, int tick, boolean actions) {
        super(filename);

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