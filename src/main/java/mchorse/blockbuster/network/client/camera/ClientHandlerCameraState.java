package mchorse.blockbuster.network.client.camera;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.camera.PacketCameraState;
import net.minecraft.client.entity.EntityPlayerSP;

/**
 * Client handler camera state
 *
 * This client handler is responsible for running or stopping the camera. It
 * is pretty simple handler.
 */
public class ClientHandlerCameraState extends ClientMessageHandler<PacketCameraState>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketCameraState message)
    {
        if (message.toPlay)
        {
            ClientProxy.profileRunner.start();
        }
        else
        {
            ClientProxy.profileRunner.stop();
        }
    }
}