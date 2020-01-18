package mchorse.blockbuster.network.server.scene;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.common.scene.PacketSceneRecord;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class ServerHandlerSceneRecord extends ServerMessageHandler<PacketSceneRecord>
{
    @Override
    public void run(EntityPlayerMP player, PacketSceneRecord message)
    {
        if (message.location.isDirector())
        {
            TileEntity te = player.world.getTileEntity(message.location.getDirector());

            if (te instanceof TileEntityDirector)
            {
                ((TileEntityDirector) te).startRecording(message.record, player);
            }
        }
        else if (message.location.isScene())
        {
            CommonProxy.scenes.record(message.location.getScene(), message.record, player);
        }
    }
}