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
        if (message.isDirector())
        {
            TileEntity te = player.worldObj.getTileEntity(message.pos);

            if (te instanceof TileEntityDirector)
            {
                ((TileEntityDirector) te).startRecording(message.record, player);
            }
        }
        else
        {
            CommonProxy.scenes.record(message.filename, message.record, player);
        }
    }
}