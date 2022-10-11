package mchorse.blockbuster.network.server.recording;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.client.recording.ClientHandlerFramesLoad;
import mchorse.blockbuster.network.common.recording.PacketRequestRecording;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.data.Record;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ServerHandlerRequestRecording extends ServerMessageHandler<PacketRequestRecording>
{
    @Override
    public void run(EntityPlayerMP player, PacketRequestRecording message)
    {
        if (message.getCallbackID().isPresent())
        {
            RecordUtils.sendRecordTo(message.getFilename(), player, message.getCallbackID().get());
        }
        else
        {
            RecordUtils.sendRecordTo(message.getFilename(), player);
        }
    }

    /**
     * Request recording from server
     * @param filename
     * @param consumer the consumer that should be executed after the server has sent its answer
     */
    @SideOnly(Side.CLIENT)
    public static void requestRecording(String filename, @Nullable Consumer<Record> consumer)
    {
        if (consumer != null)
        {
            int id = ClientHandlerFramesLoad.registerConsumer(consumer);

            Dispatcher.sendToServer(new PacketRequestRecording(filename, id));
        }
        else
        {
            Dispatcher.sendToServer(new PacketRequestRecording(filename));
        }
    }

    /**
     * Request recording from server
     * @param filename
     */
    @SideOnly(Side.CLIENT)
    public static void requestRecording(String filename)
    {
        requestRecording(filename, null);
    }
}