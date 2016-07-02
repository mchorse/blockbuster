package noname.blockbuster.network.client;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.entity.EntityCamera;
import noname.blockbuster.network.common.PacketCameraRecording;

@SideOnly(Side.CLIENT)
public class ClientHandlerCameraRecording extends ClientMessageHandler<PacketCameraRecording>
{
    @Override
    public void run(EntityPlayerSP player, PacketCameraRecording message)
    {
        Entity entity = player.worldObj.getEntityByID(message.id);

        if (entity instanceof EntityCamera)
        {
            EntityCamera camera = (EntityCamera) entity;

            camera.setRecording(message.recording, false);
        }
    }
}