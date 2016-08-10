package noname.blockbuster.network.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import noname.blockbuster.camera.TimeUtils;
import noname.blockbuster.network.common.PacketCameraMarker;
import noname.blockbuster.recording.Mocap;
import noname.blockbuster.recording.RecordThread;
import noname.blockbuster.recording.actions.ChatAction;

public class ServerHandlerCameraMarker extends ServerMessageHandler<PacketCameraMarker>
{
    @Override
    public void run(EntityPlayerMP player, PacketCameraMarker message)
    {
        RecordThread record = Mocap.records.get(player);

        if (record == null) return;

        long time = System.currentTimeMillis() - record.startTime;
        String string = String.format("§c/-§r Marker by §4%s§r: §2%d§r", record.filename, TimeUtils.formatMillis(time));

        record.eventList.add(new ChatAction(string));
        player.addChatMessage(new TextComponentString(string));
    }
}