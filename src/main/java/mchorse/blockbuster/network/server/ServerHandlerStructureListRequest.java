package mchorse.blockbuster.network.server;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.structure.PacketStructureList;
import mchorse.blockbuster.network.common.structure.PacketStructureListRequest;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerStructureListRequest extends ServerMessageHandler<PacketStructureListRequest>
{
    @Override
    public void run(EntityPlayerMP player, PacketStructureListRequest message)
    {
        Dispatcher.sendTo(new PacketStructureList(ServerHandlerStructureRequest.getAllStructures()), player);
    }
}