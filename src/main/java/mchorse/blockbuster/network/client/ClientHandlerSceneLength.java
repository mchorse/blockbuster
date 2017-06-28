package mchorse.blockbuster.network.client;

import mchorse.aperture.ClientProxy;
import mchorse.blockbuster.common.CameraHandler;
import mchorse.blockbuster.network.common.camera.PacketSceneLength;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerSceneLength extends ClientMessageHandler<PacketSceneLength>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketSceneLength message)
    {
        ClientProxy.cameraEditor.maxScrub = message.length;
        ClientProxy.cameraEditor.scrub.value = CameraHandler.tick;
        ClientProxy.cameraEditor.updateValues();
    }
}