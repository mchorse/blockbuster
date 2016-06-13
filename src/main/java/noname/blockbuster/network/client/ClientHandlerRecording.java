package noname.blockbuster.network.client;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import noname.blockbuster.entity.CameraEntity;
import noname.blockbuster.network.common.PacketRecording;

public class ClientHandlerRecording extends ClientMessageHandler<PacketRecording>
{
    @Override
    public void run(EntityPlayerSP player, PacketRecording message)
    {
        Entity entity = player.worldObj.getEntityByID(message.id);

        if (entity instanceof CameraEntity)
        {
            CameraEntity camera = (CameraEntity) entity;

            camera.setRecording(message.recording, false);
        }
    }
}