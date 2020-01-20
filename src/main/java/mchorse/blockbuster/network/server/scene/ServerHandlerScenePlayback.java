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
        if (message.location.isDirector())
        {
            TileEntity te = player.world.getTileEntity(message.location.getPosition());

            if (te instanceof TileEntityDirector)
            {
                ((TileEntityDirector) te).director.togglePlayback();
            }
        }
        else if (message.location.isScene())
        {
            CommonProxy.scenes.toggle(message.location.getFilename(), player.world);
        }
    }
}