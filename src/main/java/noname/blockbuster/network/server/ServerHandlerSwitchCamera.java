package noname.blockbuster.network.server;

import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.entity.EntityCamera;
import noname.blockbuster.network.common.PacketSwitchCamera;

/**
 * Server handler switch camera
 *
 * This handler teleports player between cameras either in forward or backward.
 */
public class ServerHandlerSwitchCamera extends ServerMessageHandler<PacketSwitchCamera>
{
    @Override
    public void run(EntityPlayerMP player, PacketSwitchCamera message)
    {
        if (!(player.getRidingEntity() instanceof EntityCamera))
        {
            return;
        }

        EntityCamera camera = (EntityCamera) player.getRidingEntity();

        camera.switchTo(message.direction);
    }
}