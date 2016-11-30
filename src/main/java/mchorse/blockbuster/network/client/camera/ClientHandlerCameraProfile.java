package mchorse.blockbuster.network.client.camera;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mchorse.blockbuster.camera.CameraProfile;
import mchorse.blockbuster.camera.CameraUtils;
import mchorse.blockbuster.commands.CommandCamera;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.camera.PacketCameraProfile;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.client.entity.EntityPlayerSP;

/**
 * Client handler camera profile
 *
 * This handler is responsible for loading the camera profile received from
 * the server into static field of {@link CommandCamera} (I think it should
 * be transfered to {@link ClientProxy}), and starting the camera profile
 * if the server inform us to.
 */
public class ClientHandlerCameraProfile extends ClientMessageHandler<PacketCameraProfile>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketCameraProfile message)
    {
        CameraProfile profile = CameraUtils.cameraJSONBuilder(false).fromJson(message.profile, CameraProfile.class);

        profile.setFilename(message.filename);
        CommandCamera.setProfile(profile);

        if (message.play)
        {
            ClientProxy.profileRunner.start();
        }

        L10n.success(player, "profile.load", message.filename);
    }
}