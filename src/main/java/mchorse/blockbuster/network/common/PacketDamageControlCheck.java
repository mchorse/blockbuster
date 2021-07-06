package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketDamageControlCheck implements IMessage
{
public BlockPos pointPos;
    
    public PacketDamageControlCheck() {
        pointPos = null;
    }
    
    public PacketDamageControlCheck(BlockPos pointPos) {
        this.pointPos = pointPos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        boolean havePointPos = buf.readBoolean();
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        if (havePointPos)
            pointPos = new BlockPos(x, y, z);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(pointPos != null);
        buf.writeInt(pointPos != null ? pointPos.getX() : 0);
        buf.writeInt(pointPos != null ? pointPos.getY() : 0);
        buf.writeInt(pointPos != null ? pointPos.getZ() : 0);
    }
}
