package mchorse.blockbuster.network.server.recording.actions;

import java.util.List;

import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.network.common.recording.actions.PacketAction;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerAction extends ServerMessageHandler<PacketAction>
{
    @Override
    public void run(EntityPlayerMP player, PacketAction message)
    {
        Record record = CommonProxy.manager.records.get(message.filename);

        if (record == null)
        {
            return;
        }

        int size = record.actions.size();

        if (message.tick >= 0 && message.tick < size)
        {
            List<Action> actions = record.actions.get(message.tick);

            if (message.index == -1)
            {
                record.actions.set(message.index, null);
            }
            else if (message.index >= 0 && message.index < actions.size())
            {
                actions.set(message.index, message.action);
            }

            record.dirty = true;
        }
    }
}