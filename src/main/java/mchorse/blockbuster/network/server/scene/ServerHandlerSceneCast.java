package mchorse.blockbuster.network.server.scene;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.common.scene.PacketSceneCast;
import mchorse.blockbuster.recording.scene.Director;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class ServerHandlerSceneCast extends ServerMessageHandler<PacketSceneCast>
{
    @Override
    public void run(EntityPlayerMP player, PacketSceneCast message)
    {
        if (message.location.isEmpty())
        {
            return;
        }

        if (message.location.isDirector())
        {
            TileEntity tile = this.getTE(player, message.location.getPosition());

            if (tile instanceof TileEntityDirector)
            {
                ((TileEntityDirector) tile).director.copy(message.location.getDirector());
                tile.markDirty();
            }

            Recording.get(player).setLastScene("");
        }
        else if (message.location.isScene())
        {
            try
            {
                CommonProxy.scenes.save(message.location.getFilename(), message.location.getScene());
                Recording.get(player).setLastScene(message.location.getFilename());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}