package noname.blockbuster.network.client;

import net.minecraft.client.entity.EntityPlayerSP;
import noname.blockbuster.ClientProxy;
import noname.blockbuster.network.common.PacketUpdateProfile;

public class ClientHandlerUpdateProfile extends ClientMessageHandler<PacketUpdateProfile>
{
    @Override
    public void run(EntityPlayerSP player, PacketUpdateProfile message)
    {
        ClientProxy.profileRunner.setProfile(message.profile);
    }
}
