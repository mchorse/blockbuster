package mchorse.blockbuster.actor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mchorse.blockbuster.client.ActorsPack;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModels;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

/**
 * This class responsible for storing domain custom models and sending and
 * receiving models from player.
 */
public class ModelHandler
{
    public Map<String, Model> models = new HashMap<String, Model>();

    /**
     * When player is logs in, send him available models and skins
     */
    @SubscribeEvent
    public void onPlayerLogsIn(PlayerLoggedInEvent event)
    {
        if (!(event.player instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        String file = DimensionManager.getCurrentSaveRootDirectory() + "/blockbuster/models";
        ActorsPack pack = new ActorsPack(file, file);
        PacketModels message = new PacketModels();

        for (String model : pack.getModels())
        {
            if (model.equals("steve") || model.equals("alex")) continue;

            Map<String, ByteBuf> skinMap = new HashMap<String, ByteBuf>();

            try
            {
                message.models.put(model, this.fileToString(file + "/" + model + "/model.json"));

                for (String skin : pack.getSkins(model))
                {
                    skinMap.put(skin, this.fileToBuffer(file + "/" + model + "/skins/" + skin + ".png"));
                }

                message.skins.put(model, skinMap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        Dispatcher.sendTo(message, player);
    }

    /**
     * Convert file to string
     */
    private String fileToString(String file) throws IOException
    {
        FileInputStream input = new FileInputStream(file);
        StringBuilder builder = new StringBuilder();
        int letter;

        while ((letter = input.read()) != -1)
        {
            builder.append((char) letter);
        }

        input.close();

        return builder.toString();
    }

    /**
     * Convert file to netty's byte buffer
     */
    private ByteBuf fileToBuffer(String file) throws IOException
    {
        byte[] bytes = IOUtils.toByteArray(new FileInputStream(file));

        return Unpooled.wrappedBuffer(bytes);
    }
}
