package mchorse.blockbuster.network.common.scene;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSceneRecord extends PacketScene
{
    public String record = "";

    public PacketSceneRecord()
    {}

    public PacketSceneRecord(BlockPos pos, String record)
    {
        super(pos);

        this.record = record;
    }

    public PacketSceneRecord(String filename, String record)
    {
        super(filename);

        this.record = record;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);

        this.record = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);

        ByteBufUtils.writeUTF8String(buf, this.record);
    }
}