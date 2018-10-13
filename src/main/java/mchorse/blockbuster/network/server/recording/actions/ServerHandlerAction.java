package mchorse.blockbuster.network.server.recording.actions;

import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.network.common.recording.actions.PacketAction;
import mchorse.blockbuster.recording.data.Record;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerAction extends ServerMessageHandler<PacketAction>
{
    @Override
    public void run(EntityPlayerMP player, PacketAction message)
    {
        Record record = null;

        try
        {
            record = CommonProxy.manager.getRecord(message.filename);
        }
        catch (Exception e)
        {}

        if (record == null)
        {
            return;
        }

        int size = record.actions.size();

        if (message.tick >= 0 && message.tick < size)
        {
            /* Remove an action*/
            if (message.action == null)
            {
                record.removeAction(message.tick, message.index);
            }
            /* Add an action */
            else if (message.add)
            {
                record.addAction(message.tick, message.index, message.action);
            }
            /* Edit an action */
            else
            {
                record.actions.get(message.tick).set(message.index, message.action);
            }

            record.dirty = true;
        }
    }
}