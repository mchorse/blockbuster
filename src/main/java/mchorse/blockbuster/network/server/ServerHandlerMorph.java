package mchorse.blockbuster.network.server;

import java.util.List;

import mchorse.blockbuster.capabilities.morphing.IMorphing;
import mchorse.blockbuster.capabilities.morphing.Morphing;
import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketMorph;
import mchorse.blockbuster.network.common.PacketMorphPlayer;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.MorphAction;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerMorph extends ServerMessageHandler<PacketMorph>
{
    @Override
    public void run(EntityPlayerMP player, PacketMorph message)
    {
        IMorphing capability = Morphing.get(player);

        if (capability != null)
        {
            capability.setModel(message.model);
            capability.setSkin(message.skin);

            Dispatcher.sendTo(message, player);
            Dispatcher.sendToTracked(player, new PacketMorphPlayer(player.getEntityId(), message.model, message.skin));
        }

        List<Action> actions = CommonProxy.manager.getActions(player);

        if (actions != null)
        {
            actions.add(new MorphAction(message.model, message.skin));
        }
    }
}