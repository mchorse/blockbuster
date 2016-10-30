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

        String difference = Integer.toString(record.tick - record.previousTick);
        String tick = Integer.toString(record.tick);
        String string = String.format("§6§l|§r Record §7%s§r, §7%s§6t§r §8(§rdifference §7%s§6t§r§8)§r.", record.record.filename, tick, difference);

        CommonProxy.manager.getActions(player).add(new ChatAction(string));
        player.addChatMessage(new TextComponentString(string));
        record.previousTick = record.tick;
    }
}