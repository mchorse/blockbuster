package noname.blockbuster.network.server;

import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.entity.CameraEntity;
import noname.blockbuster.network.common.PacketSwitchCamera;

public class ServerHandlerSwitchCamera extends ServerMessageHandler<PacketSwitchCamera>
{
    @Override
    public void run(EntityPlayerMP player, PacketSwitchCamera message)
    {
        if (!(player.getRidingEntity() instanceof CameraEntity))
        {
            return;
        }

        CameraEntity camera = (CameraEntity) player.getRidingEntity();

        camera.switchTo(message.direction);
    }
}
