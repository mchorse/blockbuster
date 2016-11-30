package mchorse.blockbuster.network.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mchorse.blockbuster.capabilities.morphing.IMorphing;
import mchorse.blockbuster.capabilities.morphing.Morphing;
import mchorse.blockbuster.network.common.PacketMorph;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientHandlerMorph extends ClientMessageHandler<PacketMorph>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketMorph message)
    {
        IMorphing capability = Morphing.get(player);

        if (capability != null)
        {
            capability.setModel(message.model);
            capability.setSkin(message.skin);
        }
    }
}
