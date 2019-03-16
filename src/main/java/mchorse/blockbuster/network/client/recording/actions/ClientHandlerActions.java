package mchorse.blockbuster.network.client.recording.actions;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.network.common.recording.actions.PacketActions;
import mchorse.blockbuster.recording.data.Record;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerActions extends ClientMessageHandler<PacketActions>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketActions message)
    {
        Record record = ClientProxy.manager.records.get(message.filename);

        if (record == null)
        {
            record = new Record(message.filename);
            ClientProxy.manager.records.put(message.filename, record);
        }

        if (record != null)
        {
            record.actions = message.actions;

            if (ClientProxy.dashboard != null && ClientProxy.dashboard.recordingEditorPanel != null)
            {
                ClientProxy.dashboard.recordingEditorPanel.selectRecord(record);
            }
        }
    }
}