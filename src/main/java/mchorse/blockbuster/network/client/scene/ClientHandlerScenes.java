package mchorse.blockbuster.network.client.scene;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.gui.dashboard.GuiBlockbusterPanels;
import mchorse.blockbuster.network.common.scene.PacketScenes;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerScenes extends ClientMessageHandler<PacketScenes>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP entityPlayerSP, PacketScenes packetScenes)
    {
        GuiBlockbusterPanels dashboard = ClientProxy.panels;

        if (dashboard.scenePanel != null)
        {
            dashboard.scenePanel.scenes.add(packetScenes.scenes);
        }
    }
}