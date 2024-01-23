package mchorse.blockbuster.network.server;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.network.common.PacketReloadModels;
import mchorse.blockbuster.utils.L10n;
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

            L10n.success(player, "model.reload");
            @SubscribeEvent
 
            event.player.addChatMessage(new ChatComponentText( "sucess!" + "blockbuster.success." + "model.reload" + "objects: NULL")
        }
        else
        {
            L10n.error(player, "model.reload");
            event.player.addChatMessage(new ChatComponentText( "failure!" + "blockbuster.error." + "model.reload" + "objects: NULL")

        }
    }
}
