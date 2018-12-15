package mchorse.blockbuster.network.client.recording.actions;

import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.common.recording.actions.PacketActions;
import mchorse.blockbuster.recording.data.Record;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerActions extends ClientMessageHandler<PacketActions>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketActions message)
    {
        Record record = new Record(message.filename);

        if (record != null)
        {
            record.actions = message.actions;

            GuiScreen screen = Minecraft.getMinecraft().currentScreen;

            if (ClientProxy.dashboard != null)
            {
                ClientProxy.dashboard.recordingEditorPanel.selectRecord(record);
            }
        }
    }
}