package mchorse.blockbuster.aperture.network.client;

import mchorse.aperture.client.gui.dashboard.GuiCameraDashboard;
import mchorse.aperture.client.gui.dashboard.GuiCameraEditor;
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
        GuiCameraEditor editor = GuiCameraDashboard.getCameraEditor().camera;

        editor.maxScrub = message.length;
        editor.dashboard.timeline.value = CameraHandler.tick;
        editor.updateValues();

        // GuiDirectorConfigOptions.getInstance().audioShift.setValue(message.shift);
    }
}