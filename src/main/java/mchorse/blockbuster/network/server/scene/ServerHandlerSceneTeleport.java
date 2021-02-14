package mchorse.blockbuster.network.server.scene;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.network.common.scene.PacketSceneTeleport;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerSceneTeleport extends ServerMessageHandler<PacketSceneTeleport>
{
    @Override
    public void run(EntityPlayerMP player, PacketSceneTeleport message)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }

        try
        {
            int tick = message.offset;
            String filename = message.id;
            Record record = CommandRecord.getRecord(filename);
            Frame frame = record.frames.get(tick);

            player.connection.setPlayerLocation(frame.x, frame.y, frame.z, frame.yaw, frame.pitch);
        }
        catch (Exception e)
        {}
    }
}