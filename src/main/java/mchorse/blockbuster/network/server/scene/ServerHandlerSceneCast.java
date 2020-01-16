package mchorse.blockbuster.network.server.scene;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.common.scene.PacketSceneCast;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.recording.director.Director;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class ServerHandlerSceneCast extends ServerMessageHandler<PacketSceneCast>
{
    @Override
    public void run(EntityPlayerMP player, PacketSceneCast message)
    {
        if (message.isDirector())
        {
            TileEntity tile = this.getTE(player, message.pos);

            if (tile instanceof TileEntityDirector)
            {
                ((TileEntityDirector) tile).director.copy((Director) message.scene);
                tile.markDirty();
            }

            Recording.get(player).setLastScene("");
        }
        else
        {
            try
            {
                CommonProxy.scenes.save(message.filename, message.scene);
                Recording.get(player).setLastScene(message.filename);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}