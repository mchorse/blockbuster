package mchorse.blockbuster.network.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mchorse.blockbuster.capabilities.morphing.IMorphing;
import mchorse.blockbuster.capabilities.morphing.Morphing;
import mchorse.blockbuster.network.common.PacketMorphPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;

public class ClientHandlerMorphPlayer extends ClientMessageHandler<PacketMorphPlayer>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketMorphPlayer message)
    {
        Entity entity = player.worldObj.getEntityByID(message.id);
        IMorphing capability = Morphing.get(player);

        if (capability != null)
        {
            capability.setModel(message.model);
            capability.setSkin(message.skin);
        }
    }
}
