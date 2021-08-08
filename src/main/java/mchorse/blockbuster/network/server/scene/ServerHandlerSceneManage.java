package mchorse.blockbuster.network.server.scene;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.PacketSceneManage;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.recording.scene.Replay;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;

import java.io.IOException;

public class ServerHandlerSceneManage extends ServerMessageHandler<PacketSceneManage>
{
    @Override
    public void run(EntityPlayerMP player, PacketSceneManage message)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }

        if (message.action == PacketSceneManage.RENAME && CommonProxy.scenes.rename(message.source, message.destination))
        {
            Dispatcher.sendTo(message, player);
        }
        else if (message.action == PacketSceneManage.REMOVE && CommonProxy.scenes.remove(message.source))
        {
            Dispatcher.sendTo(message, player);
        }
        else if (message.action == PacketSceneManage.DUPE)
        {
            Scene source = CommonProxy.scenes.get(message.source, player.getEntityWorld());

            Scene destinationDummy = new Scene();

            destinationDummy.copy(source);
            destinationDummy.setId(message.destination);
            destinationDummy.setupIds();
            destinationDummy.renamePrefix(source.getId(), destinationDummy.getId(), (id) -> id + "_copy");

            for(int i = 0; i<destinationDummy.replays.size(); i++)
            {
                Replay replaySource = source.replays.get(i);
                Replay replayDestination = destinationDummy.replays.get(i);

                int counter = 0;

                try
                {
                    Record record = CommandRecord.getRecord(replaySource.id).clone();

                    if (RecordUtils.isReplayExists(replayDestination.id))
                    {
                        continue;
                    }

                    /* This could potentially cause renaming problems like _1_2_3_4 indexes
                    while(RecordUtils.isReplayExists(replayDestination.id + ((counter != 0) ? "_"+Integer.toString(counter) : "")))
                    {
                        counter++;
                    }*/

                    record.filename = replayDestination.id + ((counter != 0) ? "_"+Integer.toString(counter) : "");
                    replayDestination.id = record.filename;

                    record.save(RecordUtils.replayFile(record.filename));

                    CommonProxy.manager.records.put(record.filename, record);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            Dispatcher.sendTo(message, player);
        }
    }
}