package mchorse.blockbuster.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.ModelPack.ModelEntry;
import mchorse.blockbuster.common.ClientProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
    public Map<String, ModelCell> models = new HashMap<String, ModelCell>();

    /**
     * Actors pack from which ModelHandler loads its models
     */
    public ModelPack pack;

    /**
     * Load user and default provided models into model map
     */
    public void loadModels(ModelPack pack)
    {
        pack.reload();

        /* Keys which are going to be used to determine whether the 
         * model was removed */
        Set<String> keys = new HashSet<String>(this.models.keySet());

        /* Load user provided models */
        for (String model : pack.getModels())
        {
            ModelEntry entry = pack.models.get(model);
            ModelCell cell = this.models.get(model);
            long timestamp = entry.lastModified();

            keys.remove(model);

            /* Whether the mod should be reloaded */
            if (cell != null && timestamp <= cell.timestamp)
            {
                continue;
            }

            try
            {
                if (entry.customModel == null)
                {
                    /* Generate custom model for an OBJ model */
                    InputStream modelStream = this.getClass().getClassLoader().getResourceAsStream("assets/blockbuster/models/entity/obj.json");
                    Model data = Model.parse(modelStream);

                    data.name = model;

                    this.models.put(model, new ModelCell(data, timestamp));
                    modelStream.close();
                }
                else
                {
                    InputStream modelStream = new FileInputStream(entry.customModel);

                    this.models.put(model, new ModelCell(Model.parse(modelStream), timestamp));
                    modelStream.close();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        for (String key : keys)
        {
            this.models.remove(key);
            System.out.println("Removing '" + key + "' custom model!");
        }

        /* Load default provided models */
        try
        {
            String path = "assets/blockbuster/models/entity/";
            ClassLoader loader = this.getClass().getClassLoader();

            /* Optionally load default models */
            if (!this.models.containsKey("alex"))
            {
                this.models.put("alex", new ModelCell(Model.parse(loader.getResourceAsStream(path + "alex.json")), 0));
            }

            if (!this.models.containsKey("steve"))
            {
                this.models.put("steve", new ModelCell(Model.parse(loader.getResourceAsStream(path + "steve.json")), 0));
            }

            if (!this.models.containsKey("fred"))
            {
                this.models.put("fred", new ModelCell(Model.parse(loader.getResourceAsStream(path + "fred.json")), 0));
            }

            this.models.put("yike", new ModelCell(Model.parse(loader.getResourceAsStream(path + "yike.json")), 0));
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

    public static class ModelCell
    {
        public Model model;
        public long timestamp;
        public boolean load = true;

        public ModelCell(Model model, long timestamp)
        {
            this.model = model;
            this.timestamp = timestamp;
        }
    }
}