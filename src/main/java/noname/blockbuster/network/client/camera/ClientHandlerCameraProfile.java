package noname.blockbuster.network.client.camera;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextComponentTranslation;
import noname.blockbuster.ClientProxy;
import noname.blockbuster.commands.CommandCamera;
import noname.blockbuster.network.client.ClientMessageHandler;
import noname.blockbuster.network.common.camera.PacketCameraProfile;

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

        player.addChatMessage(new TextComponentTranslation("blockbuster.profile.load", message.filename));
    }
}