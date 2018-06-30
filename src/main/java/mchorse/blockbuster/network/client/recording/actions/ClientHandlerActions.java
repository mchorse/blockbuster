package mchorse.blockbuster.network.client.recording.actions;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.recording.actions.PacketActions;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;

public class ClientHandlerActions extends ClientMessageHandler<PacketActions>
{
    @Override
    public void run(EntityPlayerSP player, PacketActions message)
    {
        Record record = new Record(message.filename);

        if (record != null)
        {
            record.actions = message.actions;

            GuiScreen screen = Minecraft.getMinecraft().currentScreen;

            if (screen instanceof GuiDashboard)
            {
                ((GuiDashboard) screen).recordingEditorPanel.selectRecord(record);
            }
        }
    }
}