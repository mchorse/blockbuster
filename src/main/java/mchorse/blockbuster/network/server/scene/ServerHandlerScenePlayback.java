package mchorse.blockbuster.network.server.scene;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.common.scene.PacketScenePlayback;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class ServerHandlerScenePlayback extends ServerMessageHandler<PacketScenePlayback>
{
    @Override
    public void run(EntityPlayerMP player, PacketScenePlayback message)
    {
        if (message.isDirector())
        {
            TileEntity te = player.worldObj.getTileEntity(message.pos);

            if (te instanceof TileEntityDirector)
            {
                ((TileEntityDirector) te).director.togglePlayback();
            }
        }
        else
        {
            CommonProxy.scenes.toggle(message.filename, player.worldObj);
        }
    }
}