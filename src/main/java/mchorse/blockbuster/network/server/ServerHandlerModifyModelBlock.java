package mchorse.blockbuster.network.server;

import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.network.common.PacketModifyModelBlock;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class ServerHandlerModifyModelBlock extends ServerMessageHandler<PacketModifyModelBlock>
{
    @Override
    public void run(EntityPlayerMP player, PacketModifyModelBlock message)
    {
        TileEntity tile = player.worldObj.getTileEntity(message.pos);

        if (tile != null && tile instanceof TileEntityModel)
        {
            TileEntityModel model = (TileEntityModel) tile;

            model.rotateX = message.rotateX;
            model.rotateY = message.rotateY;
            model.x = message.x;
            model.y = message.y;
            model.z = message.z;
            model.setMorph(message.morph);
        }
    }
}