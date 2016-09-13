package mchorse.blockbuster.actor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModels;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

/**
 * This class responsible for storing domain custom models and sending models to
 * players who are logged in.
 */
public class ModelHandler
{
    /**
     * Cached models, they're loaded from stuffs
     */
    public Map<String, Model> models = new HashMap<String, Model>();

    /**
     * Actors pack from which ModelHandler loads its models
     */
    public ActorsPack pack;

    public ModelHandler(ActorsPack pack)
    {
        this.pack = pack;
        this.pack.reload();
    }

    /**
     * Load models into models storage
     */
    public void loadModels(ActorsPack pack)
    {
        pack.reload();

        for (String model : pack.getModels())
        {
            ResourceLocation resource = new ResourceLocation("blockbuster.actors", model);

            try
            {
                this.models.put(model, Model.parse(pack.getInputStream(resource)));
            }
            catch (Exception e)
            {
                System.out.println("Model by key \"" + model + "\" couldn't be parsed and loaded!");
                e.printStackTrace();
            }
        }

        try
        {
            this.models.put("alex", Model.parse(this.getClass().getClassLoader().getResourceAsStream("assets/blockbuster/models/entity/alex.json")));
            this.models.put("steve", Model.parse(this.getClass().getClassLoader().getResourceAsStream("assets/blockbuster/models/entity/steve.json")));
        }
        catch (Exception e)
        {
            System.out.println("Default provided models couldn't be loaded!");
            e.printStackTrace();
        }
    }

    /**
     * When player is logs in, send him available models and skins
     */
    @SubscribeEvent
    public void onPlayerLogsIn(PlayerLoggedInEvent event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        PacketModels message = new PacketModels();

        for (String model : this.pack.getModels())
        {
            try
            {
                Map<String, ByteBuf> skinMap = new HashMap<String, ByteBuf>();

                for (String skin : this.pack.getSkins(model))
                {
                    skinMap.put(skin, this.fileToBuffer(this.pack.getInputStream(new ResourceLocation(model + "/" + skin))));
                }

                message.skins.put(model, skinMap);

                if (model.equals("alex") || model.equals("steve"))
                {
                    continue;
                }

                message.models.put(model, this.fileToString(this.pack.getInputStream(new ResourceLocation(model))));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        Dispatcher.sendTo(message, player);
    }

    /**
     * Convert file into string
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