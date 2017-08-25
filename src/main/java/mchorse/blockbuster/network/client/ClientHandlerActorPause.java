package mchorse.blockbuster.network.client;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.common.PacketActorPause;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerActorPause extends ClientMessageHandler<PacketActorPause>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketActorPause message)
    {
        EntityActor actor = (EntityActor) player.worldObj.getEntityByID(message.id);

        if (actor.playback != null)
        {
            actor.playback.tick = message.tick;
            actor.playback.playing = !message.pause;
            actor.playback.record.applyFrame(message.tick, actor, true);
        }
    }
}