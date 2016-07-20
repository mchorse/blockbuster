package noname.blockbuster.network.client;

import net.minecraft.client.entity.EntityPlayerSP;
import noname.blockbuster.ClientProxy;
import noname.blockbuster.commands.CommandCamera;
import noname.blockbuster.network.common.PacketCameraProfile;

public class ClientHandlerCameraProfile extends ClientMessageHandler<PacketCameraProfile>
{
    @Override
    public void run(EntityPlayerSP player, PacketCameraProfile message)
    {
        CommandCamera.setProfile(message.profile);

        if (message.play)
        {
            ClientProxy.profileRunner.start();
        }
    }
}