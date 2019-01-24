package mchorse.blockbuster.network.client.recording.actions;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.network.common.recording.actions.PacketActionList;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerActionList extends ClientMessageHandler<PacketActionList>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketActionList message)
    {
        if (ClientProxy.dashboard != null)
        {
            ClientProxy.dashboard.recordingEditorPanel.addRecords(message.records);
        }
    }
}