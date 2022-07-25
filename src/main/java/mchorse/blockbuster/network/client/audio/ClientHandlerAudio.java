package mchorse.blockbuster.network.client.audio;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.network.common.audio.PacketAudio;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerAudio extends ClientMessageHandler<PacketAudio>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketAudio message)
    {
        ClientProxy.audio.handleAudio(message.audio, message.state, message.shift, message.delay);
    }
}