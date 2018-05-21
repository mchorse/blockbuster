package mchorse.blockbuster.api;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.ModelPack.ModelEntry;
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
    public void loadModels(ModelPack pack, boolean force)
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

            /* Whether the model should be reloaded */
            if (!force && cell != null && timestamp <= cell.timestamp)
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

                    this.addModel(model, new ModelCell(data, timestamp));
                    modelStream.close();
                }
                else
                {
                    InputStream modelStream = new FileInputStream(entry.customModel);

                    this.addModel(model, new ModelCell(Model.parse(modelStream), timestamp));
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
            this.removeModel(key);
        }

        /* Load default provided models */
        try
        {
            String path = "assets/blockbuster/models/entity/";
            ClassLoader loader = this.getClass().getClassLoader();

            /* Optionally load default models */
            if (!this.models.containsKey("alex"))
            {
                this.addModel("alex", new ModelCell(Model.parse(loader.getResourceAsStream(path + "alex.json")), 0));
            }

            if (!this.models.containsKey("steve"))
            {
                this.addModel("steve", new ModelCell(Model.parse(loader.getResourceAsStream(path + "steve.json")), 0));
            }

            if (!this.models.containsKey("fred"))
            {
                this.addModel("fred", new ModelCell(Model.parse(loader.getResourceAsStream(path + "fred.json")), 0));
            }

            this.addModel("yike", new ModelCell(Model.parse(loader.getResourceAsStream(path + "yike.json")), 0));
            keys.remove("yike");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Add model to the model handler 
     */
    protected void addModel(String name, ModelCell cell)
    {
        this.models.put(name, cell);
    }

    /**
     * Remove model from model handler 
     */
    protected void removeModel(String key)
    {
        this.models.remove(key);
        System.out.println("Removing '" + key + "' custom model!");
    }

    /**
     * Clear models when disconnecting from server
     */
    @SubscribeEvent
    public void onClientDisconnect(ClientDisconnectionFromServerEvent event)
    {
        this.models.clear();
    }

    /**
     * Loads local models when connecting to the server
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientConnect(ClientConnectedToServerEvent event)
    {
        Blockbuster.proxy.loadModels(Blockbuster.proxy.getPack(), false);
    }

    public static class ModelCell
    {
        public Model model;
        public long timestamp;

        public ModelCell(Model model, long timestamp)
        {
            this.model = model;
            this.timestamp = timestamp;
        }
    }
}