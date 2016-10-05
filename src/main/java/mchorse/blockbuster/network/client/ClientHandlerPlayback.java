package mchorse.blockbuster.network.client;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.common.recording.PacketPlayback;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerPlayback extends ClientMessageHandler<PacketPlayback>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketPlayback message)
    {
        EntityActor actor = (EntityActor) player.worldObj.getEntityByID(message.id);

        if (!message.state)
        {
            actor.playback = null;
        }
    }
}
