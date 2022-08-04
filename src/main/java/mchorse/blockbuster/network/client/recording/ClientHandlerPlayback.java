package mchorse.blockbuster.network.client.recording;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.PacketPlayback;
import mchorse.blockbuster.network.common.recording.PacketRequestFrames;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.EntityUtils;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Client handler actor playback
 *
 * This client handler is responsible for starting actor playback. There are
 * few cases to consider.
 *
 * If the message says to stop playback, it is quite simple, but for start
 * playback there are few checks required to be made.
 *
 * If record exists on the client, we'll simply create new record player and
 * request tick and delay, just in case, for synchronization purpose, but if
 * client doesn't have a record, it should request the server to provide one.
 */
public class ClientHandlerPlayback extends ClientMessageHandler<PacketPlayback>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketPlayback message)
    {
        EntityLivingBase actor = (EntityLivingBase) player.world.getEntityByID(message.id);

        if (message.state)
        {
            Record record = ClientProxy.manager.getClient(message.filename);
            RecordPlayer recordPlayer = EntityUtils.getRecordPlayer(actor);

            if (recordPlayer == null)
            {
                recordPlayer = new RecordPlayer(record, Mode.FRAMES, actor);

                recordPlayer.setReplay(message.replay);

                if (message.realPlayer)
                {
                    recordPlayer.realPlayer();
                }

                EntityUtils.setRecordPlayer(actor, recordPlayer);
            }
            else
            {
                recordPlayer.setReplay(message.replay);
                recordPlayer.record = record;
                recordPlayer.realPlayer = message.realPlayer;
                recordPlayer.tick = 0;
            }

            if (record == null)
            {
                Dispatcher.sendToServer(new PacketRequestFrames(message.id, message.filename));
            }
        }
        else
        {
            EntityUtils.setRecordPlayer(actor, null);

            if (actor == Minecraft.getMinecraft().player)
            {
                CameraHandler.resetRoll();
            }
        }
    }
}