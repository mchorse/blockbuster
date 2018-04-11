package mchorse.blockbuster.network.server;

import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModifyModelBlock;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class ServerHandlerModifyModelBlock extends ServerMessageHandler<PacketModifyModelBlock>
{
    @Override
    public void run(EntityPlayerMP player, PacketModifyModelBlock message)
    {
        BlockPos pos = message.pos;
        TileEntity tile = player.world.getTileEntity(pos);

        if (tile != null && tile instanceof TileEntityModel)
        {
            ((TileEntityModel) tile).copyData(message);

            Dispatcher.get().sendToAllAround(message, new TargetPoint(player.dimension, pos.getX(), pos.getY(), pos.getZ(), 64));
        }
    }
}