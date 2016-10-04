package mchorse.blockbuster.network.server;

import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.network.common.PacketCameraMarker;
import mchorse.blockbuster.recording.RecordRecorder;
import mchorse.blockbuster.recording.actions.ChatAction;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

public class ServerHandlerCameraMarker extends ServerMessageHandler<PacketCameraMarker>
{
    @Override
    public void run(EntityPlayerMP player, PacketCameraMarker message)
    {
        RecordRecorder record = CommonProxy.manager.recorders.get(player);

        if (record == null) return;

        String string = String.format("§c---§r Marker by §4%s§r: §2%s ticks§r", record.record.filename, Integer.toString(record.ticks));

        CommonProxy.manager.getActions(player).add(new ChatAction(string));
        player.addChatMessage(new TextComponentString(string));
    }
}