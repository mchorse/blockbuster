package mchorse.blockbuster.network.client.recording;

import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.recording.PacketUnloadRecordings;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerUnloadRecordings extends ClientMessageHandler<PacketUnloadRecordings>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketUnloadRecordings message)
    {
        ClientProxy.manager.records.clear();
    }
}