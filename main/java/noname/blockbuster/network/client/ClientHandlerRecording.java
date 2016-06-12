package noname.blockbuster.network.client;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import noname.blockbuster.entity.CameraEntity;
import noname.blockbuster.network.common.Recording;

public class ClientHandlerRecording extends ClientMessageHandler<Recording>
{
    @Override
    public void run(EntityPlayerSP player, Recording message)
    {
        Entity entity = player.worldObj.getEntityByID(message.id);

        if (entity instanceof CameraEntity)
        {
            CameraEntity camera = (CameraEntity) entity;

            camera.setRecording(message.recording, false);
        }
    }
}