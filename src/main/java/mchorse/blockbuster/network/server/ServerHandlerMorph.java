package mchorse.blockbuster.network.server;

import mchorse.blockbuster.capabilities.morphing.IMorphing;
import mchorse.blockbuster.capabilities.morphing.MorphingProvider;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketMorph;
import mchorse.blockbuster.network.common.PacketMorphPlayer;
import mchorse.blockbuster.recording.Mocap;
import mchorse.blockbuster.recording.RecordThread;
import mchorse.blockbuster.recording.actions.MorphAction;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerMorph extends ServerMessageHandler<PacketMorph>
{
    @Override
    public void run(EntityPlayerMP player, PacketMorph message)
    {
        IMorphing capability = player.getCapability(MorphingProvider.MORPHING_CAP, null);

        if (capability != null)
        {
            capability.setModel(message.model);
            capability.setSkin(message.skin);

            Dispatcher.sendTo(message, player);
            Dispatcher.updateTrackers(player, new PacketMorphPlayer(player.getEntityId(), message.model, message.skin));
        }

        RecordThread record = Mocap.records.get(player);

        if (record != null)
        {
            record.eventList.add(new MorphAction(message.model, message.skin));
        }
    }
}