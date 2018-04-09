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