package mchorse.blockbuster.network.server.recording;

import mchorse.blockbuster.network.common.recording.PacketRequestFrames;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerRequestFrames extends ServerMessageHandler<PacketRequestFrames>
{
    @Override
    public void run(EntityPlayerMP player, PacketRequestFrames message)
    {
        RecordUtils.sendRequestedRecord(message.id, message.filename, player);
    }
}