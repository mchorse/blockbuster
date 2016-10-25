package mchorse.blockbuster.network.server.recording;

import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.PacketRequestFrames;
import mchorse.blockbuster.network.common.recording.PacketRequestedFrames;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerRequestFrames extends ServerMessageHandler<PacketRequestFrames>
{
    @Override
    public void run(EntityPlayerMP player, PacketRequestFrames message)
    {
        Record record = CommonProxy.manager.records.get(message.filename);

        if (record != null)
        {
            Dispatcher.sendTo(new PacketRequestedFrames(message.id, record.filename, record.frames), player);
        }
        else
        {
            System.out.println("Record '" + message.filename + "' couldn't be loaded, because it doesn't exist!");
        }
    }
}
