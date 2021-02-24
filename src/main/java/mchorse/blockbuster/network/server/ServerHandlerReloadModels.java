package mchorse.blockbuster.network.server;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.network.common.PacketReloadModels;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerReloadModels extends ServerMessageHandler<PacketReloadModels>
{
    @Override
    public void run(EntityPlayerMP player, PacketReloadModels message)
    {
        if (player.canUseCommand(2, ""))
        {
            Blockbuster.reloadServerModels(message.force);
            Blockbuster.l10n.success(player, "model.reload");
        }
        else
        {
            Blockbuster.l10n.error(player, "model.reload");
        }
    }
}