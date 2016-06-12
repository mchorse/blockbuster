package noname.blockbuster.network.server;

import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.entity.CameraEntity;
import noname.blockbuster.network.common.SwitchCamera;

public class ServerHandlerSwitchCamera extends ServerMessageHandler<SwitchCamera>
{
    @Override
    public void run(EntityPlayerMP player, SwitchCamera message)
    {
        if (!(player.getRidingEntity() instanceof CameraEntity))
        {
            return;
        }

        CameraEntity camera = (CameraEntity) player.getRidingEntity();

        camera.switchTo(message.direction);
    }
}
