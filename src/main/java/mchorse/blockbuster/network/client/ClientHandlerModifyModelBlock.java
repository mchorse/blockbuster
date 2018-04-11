package mchorse.blockbuster.network.client;

import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.network.common.PacketModifyModelBlock;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.tileentity.TileEntity;

public class ClientHandlerModifyModelBlock extends ClientMessageHandler<PacketModifyModelBlock>
{
    @Override
    public void run(EntityPlayerSP player, PacketModifyModelBlock message)
    {
        TileEntity tile = player.world.getTileEntity(message.pos);

        if (tile != null && tile instanceof TileEntityModel)
        {
            ((TileEntityModel) tile).copyData(message);
        }
    }
}