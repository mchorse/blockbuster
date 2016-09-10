package mchorse.blockbuster.network.server;

import mchorse.blockbuster.network.common.PacketMorph;
import mchorse.blockbuster.recording.Mocap;
import mchorse.blockbuster.recording.RecordThread;
import mchorse.blockbuster.recording.actions.MorphAction;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerMorph extends ServerMessageHandler<PacketMorph>
{
    @Override
    public void run(EntityPlayerMP player, PacketMorph message)
    {
        RecordThread record = Mocap.records.get(player);

        if (record == null) return;

        record.eventList.add(new MorphAction(message.model, message.skin));
    }
}
