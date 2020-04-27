package mchorse.blockbuster.aperture.network.client;

import mchorse.aperture.ClientProxy;
import mchorse.aperture.client.gui.GuiCameraEditor;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.aperture.network.common.PacketSceneLength;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerSceneLength extends ClientMessageHandler<PacketSceneLength>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketSceneLength message)
    {
        GuiCameraEditor editor = ClientProxy.getCameraEditor();

        editor.maxScrub = message.length;
        editor.timeline.value = CameraHandler.tick;
        editor.updateValues();
    }
}