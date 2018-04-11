package mchorse.blockbuster.network.server;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.network.common.PacketReloadModels;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerReloadModels extends ServerMessageHandler<PacketReloadModels>
{
    @Override
    public void run(EntityPlayerMP player, PacketReloadModels message)
    {
        if (player.canUseCommand(2, ""))
        {
            Blockbuster.reloadServerModels();

            L10n.success(player, "model.reload");
        }
        else
        {
            L10n.error(player, "model.reload");
        }
    }
}