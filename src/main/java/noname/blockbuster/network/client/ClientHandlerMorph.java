package noname.blockbuster.network.client;

import net.minecraft.client.entity.EntityPlayerSP;
import noname.blockbuster.ClientProxy;
import noname.blockbuster.network.common.PacketMorph;

public class ClientHandlerMorph extends ClientMessageHandler<PacketMorph>
{
    @Override
    public void run(EntityPlayerSP player, PacketMorph message)
    {
        ClientProxy.playerRender.model = message.model;
        ClientProxy.playerRender.skin = message.skin;
    }
}
