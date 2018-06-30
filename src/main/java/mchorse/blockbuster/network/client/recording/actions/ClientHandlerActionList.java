package mchorse.blockbuster.network.client.recording.actions;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.recording.actions.PacketActionList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerActionList extends ClientMessageHandler<PacketActionList>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketActionList message)
    {
        GuiScreen current = Minecraft.getMinecraft().currentScreen;

        if (current instanceof GuiDashboard)
        {
            ((GuiDashboard) current).recordingEditorPanel.addRecords(message.records);
        }
    }
}