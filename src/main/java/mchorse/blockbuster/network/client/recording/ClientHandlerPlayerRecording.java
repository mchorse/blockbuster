package mchorse.blockbuster.network.client.recording;

import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.capabilities.morphing.Morphing;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.recording.PacketFramesChunk;
import mchorse.blockbuster.network.common.recording.PacketPlayerRecording;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Client hanlder player recording
 *
 * This client handler is responsible for updating recording overlay status and
 * starting or stopping the recording based on the state given from packet.
 */
public class ClientHandlerPlayerRecording extends ClientMessageHandler<PacketPlayerRecording>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketPlayerRecording message)
    {
        ClientProxy.recordingOverlay.setVisible(message.recording);
        ClientProxy.recordingOverlay.setCaption(message.filename);

        if (message.recording)
        {
            ClientProxy.manager.startRecording(message.filename, player, Mode.FRAMES, false);
        }
        else
        {
            Morphing.get(player).reset();

            this.sendFrames(ClientProxy.manager.recorders.get(player).record);
            ClientProxy.manager.stopRecording(player, false);
        }
    }

    /**
     * Send frames to the server
     *
     * Send chunked frames to the server.
     */
    @SideOnly(Side.CLIENT)
    private void sendFrames(Record record)
    {
        int cap = 100;
        int length = record.getLength();

        /* Send only one message if it's below 500 frames */
        if (length < cap)
        {
            Dispatcher.sendToServer(new PacketFramesChunk(0, 1, record.filename, record.frames));

            return;
        }

        /* Send chunked frames to the server */
        for (int i = 0, c = (length / cap) + 1; i < c; i++)
        {
            List<Frame> frames = new ArrayList<Frame>();

            for (int j = 0, d = length - i * cap > cap ? cap : (length % cap); j < d; j++)
            {
                frames.add(record.frames.get(j + i * cap));
            }

            Dispatcher.sendToServer(new PacketFramesChunk(i, c, record.filename, frames));
        }
    }
}