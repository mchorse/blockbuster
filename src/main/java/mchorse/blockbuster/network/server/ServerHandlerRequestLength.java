package mchorse.blockbuster.network.server;

import mchorse.blockbuster.aperture.network.common.PacketRequestLength;
import mchorse.blockbuster.aperture.network.common.PacketSceneLength;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.Dispatcher;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class ServerHandlerRequestLength extends ServerMessageHandler<PacketRequestLength>
{
    @Override
    public void run(EntityPlayerMP player, PacketRequestLength message)
    {
        TileEntity tile = player.world.getTileEntity(message.pos);

        if (tile != null && tile instanceof TileEntityDirector)
        {
            Dispatcher.sendTo(new PacketSceneLength(((TileEntityDirector) tile).getMaxLength()), player);
        }
    }
}