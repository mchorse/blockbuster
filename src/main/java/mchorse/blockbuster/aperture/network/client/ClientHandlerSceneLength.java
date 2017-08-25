package mchorse.blockbuster.aperture.network.client;

import mchorse.aperture.ClientProxy;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.aperture.network.common.PacketSceneLength;
import mchorse.blockbuster.network.client.ClientMessageHandler;
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