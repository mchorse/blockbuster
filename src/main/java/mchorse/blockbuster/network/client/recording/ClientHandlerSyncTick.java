package mchorse.blockbuster.network.client.recording;

import mchorse.blockbuster.network.common.recording.PacketSyncTick;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.utils.EntityUtils;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Client handler synchronize ticks.
 *
 * This client handler is responsible for synchronizing client's ticks.
 */
public class ClientHandlerSyncTick extends ClientMessageHandler<PacketSyncTick>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketSyncTick message)
    {
        EntityLivingBase actor = (EntityLivingBase) player.world.getEntityByID(message.id);
        RecordPlayer playback = EntityUtils.getRecordPlayer(actor);

        if (playback != null && playback.record != null)
        {
            playback.tick = message.tick;

            if (!playback.playing)
            {
                playback.applyFrame(message.tick, actor, false);
            }
        }
    }
}