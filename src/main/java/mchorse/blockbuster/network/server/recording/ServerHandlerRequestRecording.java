package mchorse.blockbuster.network.server.recording;

import mchorse.blockbuster.network.common.recording.PacketRequestRecording;
import mchorse.blockbuster.recording.Utils;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerRequestRecording extends ServerMessageHandler<PacketRequestRecording>
{
    @Override
    public void run(EntityPlayerMP player, PacketRequestRecording message)
    {
        Utils.sendRecord(message.filename, player);
    }
}