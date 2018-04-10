package mchorse.blockbuster.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.ModelPack.ModelEntry;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.server.ServerHandlerRequestModels;
import mchorse.blockbuster.utils.L10n;
import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.models.Model;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    public ModelPack pack;

    /**
     * Load user and default provided models into model map
     */
    public void loadModels(ModelPack pack)
    {
        this.models.clear();
        pack.reload();

        /* Load user provided models */
        for (String model : pack.getModels())
        {
            if (ModelPack.IGNORED_MODELS.contains(model))
            {
                continue;
            }

            try
            {
                ModelEntry entry = pack.models.get(model);

                if (entry.customModel == null)
                {
                    /* Generate custom model for an OBJ model */
                    InputStream modelStream = this.getClass().getClassLoader().getResourceAsStream("assets/blockbuster/models/entity/obj.json");
                    Model data = Model.parse(modelStream);

                    data.name = model;

                    this.models.put("blockbuster." + model, data);
                    modelStream.close();
                }
                else
                {
                    InputStream modelStream = new FileInputStream(entry.customModel);

                    this.models.put("blockbuster." + model, Model.parse(modelStream));
                    modelStream.close();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        /* Load default provided models */
        try
        {
            String path = "assets/blockbuster/models/entity/";
            ClassLoader loader = this.getClass().getClassLoader();

            /* Optionally load default models */
            if (!this.models.containsKey("blockbuster.alex"))
            {
                this.models.put("blockbuster.alex", Model.parse(loader.getResourceAsStream(path + "alex.json")));
            }

            if (!this.models.containsKey("blockbuster.steve"))
            {
                this.models.put("blockbuster.steve", Model.parse(loader.getResourceAsStream(path + "steve.json")));
            }

            if (!this.models.containsKey("blockbuster.fred"))
            {
                this.models.put("blockbuster.fred", Model.parse(loader.getResourceAsStream(path + "fred.json")));
            }

            this.models.put("blockbuster.yike", Model.parse(loader.getResourceAsStream(path + "yike.json")));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Clear models when disconnecting from server
     */
    @SubscribeEvent
    public void onClientDisconnect(ClientDisconnectionFromServerEvent event)
    {
        this.models.clear();

        if (Blockbuster.proxy.config.clean_model_downloads)
        {
            try
            {
                File models = new File(ClientProxy.config + "/downloads");
                FileUtils.cleanDirectory(models);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads local models when connecting to the server
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientConnect(ClientConnectedToServerEvent event)
    {
        Blockbuster.proxy.loadModels(Blockbuster.proxy.getPack());
    }

    /**
     * When player is logs in, send him all available models and skins. I think
     * this should go to a separate server handler
     */
    @SubscribeEvent
    public void onPlayerLogsIn(PlayerLoggedInEvent event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.player;

        if (Blockbuster.proxy.config.load_models_on_login)
        {
            ServerHandlerRequestModels.sendModels(this, player);
        }

        if (!Metamorph.VERSION.equals(Blockbuster.METAMORPH))
        {
            L10n.info(player, "metamorph", Metamorph.VERSION, Blockbuster.METAMORPH);
        }
    }
}