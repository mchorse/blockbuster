package mchorse.blockbuster.network.server;

import mchorse.blockbuster.camera.TimeUtils;
import mchorse.blockbuster.network.common.PacketCameraMarker;
import mchorse.blockbuster.recording.Mocap;
import mchorse.blockbuster.recording.RecordThread;
import mchorse.blockbuster.recording.actions.ChatAction;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

public class ServerHandlerCameraMarker extends ServerMessageHandler<PacketCameraMarker>
{
    @Override
    public void run(EntityPlayerMP player, PacketCameraMarker message)
    {
        RecordThread record = Mocap.records.get(player);

        if (record == null) return;

        long time = System.currentTimeMillis() - record.startTime;
        String string = String.format("§c---§r Marker by §4%s§r: §2%s§r", record.filename, TimeUtils.formatMillis(time));

        record.eventList.add(new ChatAction(string));
        player.addChatMessage(new TextComponentString(string));
    }
}