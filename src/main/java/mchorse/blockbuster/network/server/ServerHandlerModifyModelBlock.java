package mchorse.blockbuster.network.server;

import mchorse.blockbuster.common.block.BlockModel;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModifyModelBlock;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class ServerHandlerModifyModelBlock extends ServerMessageHandler<PacketModifyModelBlock>
{
    @Override
    public void run(EntityPlayerMP player, PacketModifyModelBlock message)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }

        BlockPos pos = message.pos;
        TileEntity tile = this.getTE(player, pos);

        if (tile instanceof TileEntityModel)
        {
            ((TileEntityModel) tile).copyData(message.model, false);

            //set the blockstate in the world - important for servers
            tile.getWorld().setBlockState(message.pos, tile.getWorld().getBlockState(message.pos).withProperty(BlockModel.LIGHT, message.model.getSettings().getLightValue()) , 2);

            Dispatcher.DISPATCHER.get().sendToDimension(message, player.dimension);
        }
    }
}