package mchorse.blockbuster.aperture.network.client;

import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.aperture.network.common.PacketAperture;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientHandlerAperture extends ClientMessageHandler<PacketAperture>
{
    @Override
    public void run(EntityPlayerSP player, PacketAperture message)
    {
        CameraHandler.server = true;
    }
}