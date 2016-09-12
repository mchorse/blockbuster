package mchorse.blockbuster.actor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModels;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
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
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        String file = DimensionManager.getCurrentSaveRootDirectory() + "/blockbuster/models";
        PacketModels message = new PacketModels();

        ActorsPack pack = Blockbuster.proxy.getPack();
        pack.addFolder(file);
        pack.reload();

        for (String model : pack.getModels())
        {
            try
            {
                Map<String, ByteBuf> skinMap = new HashMap<String, ByteBuf>();

                for (String skin : pack.getSkins(model))
                {
                    skinMap.put(skin, this.fileToBuffer(pack.getInputStream(new ResourceLocation(model + "/" + skin))));
                }

                message.skins.put(model, skinMap);

                if (model.equals("alex") || model.equals("steve"))
                {
                    continue;
                }

                message.models.put(model, this.fileToString(pack.getInputStream(new ResourceLocation(model))));
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
    private String fileToString(InputStream input) throws IOException
    {
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
    private ByteBuf fileToBuffer(InputStream input) throws IOException
    {
        return Unpooled.wrappedBuffer(IOUtils.toByteArray(input));
    }
}