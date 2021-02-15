package mchorse.blockbuster.network.server.recording.actions;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.network.common.recording.actions.PacketAction;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.data.Record;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerAction extends ServerMessageHandler<PacketAction>
{
    @Override
    public void run(EntityPlayerMP player, PacketAction message)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }

        Record record = null;

        try
        {
            record = CommonProxy.manager.get(message.filename);
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
                record.replaceAction(message.tick, message.index, message.action);
            }

            try
            {
                RecordUtils.saveRecord(record, false, false);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}