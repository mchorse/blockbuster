package mchorse.blockbuster.network.server;

import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.network.common.PacketCameraMarker;
import mchorse.blockbuster.recording.RecordRecorder;
import mchorse.blockbuster.recording.actions.ChatAction;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class ServerHandlerCameraMarker extends ServerMessageHandler<PacketCameraMarker>
{
    @Override
    public void run(EntityPlayerMP player, PacketCameraMarker message)
    {
        RecordRecorder record = CommonProxy.manager.recorders.get(player);

        if (record == null) return;

        int delay = record.record.delay;

        String difference = Integer.toString((record.tick - record.previousTick) * delay);
        String tick = Integer.toString(record.tick * delay);
        String string = I18n.format("blockbuster.marker", record.record.filename, Integer.toString(delay), tick, difference);

        CommonProxy.manager.getActions(player).add(new ChatAction(string));
        player.addChatMessage(new ChatComponentText(string));
        record.previousTick = record.tick;
    }
}