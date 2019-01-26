package mchorse.blockbuster.network.client.recording;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.network.common.recording.PacketRequestedFrames;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.EntityUtils;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Client handler requested frames
 *
 * This client handler is responsible for saving received frames into the client
 * record manager and also injecting received record into the provided actor.
 */
public class ClientHandlerRequestedFrames extends ClientMessageHandler<PacketRequestedFrames>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketRequestedFrames message)
    {
        Record record = new Record(message.filename);
        record.frames = message.frames;
        record.preDelay = message.preDelay;
        record.postDelay = message.postDelay;

        ClientProxy.manager.records.put(record.filename, record);
        EntityLivingBase actor = (EntityLivingBase) player.world.getEntityByID(message.id);
        RecordPlayer playback = EntityUtils.getRecordPlayer(actor);

        if (actor != null && playback != null)
        {
            playback.record = record;
        }
    }
}