package mchorse.blockbuster.network.client;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.network.common.PacketMorph;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientHandlerMorph extends ClientMessageHandler<PacketMorph>
{
    @Override
    public void run(EntityPlayerSP player, PacketMorph message)
    {
        ClientProxy.playerRender.model = message.model;
        ClientProxy.playerRender.skin = message.skin;
    }
}
