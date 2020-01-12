package mchorse.blockbuster.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.loaders.lazy.IModelLazyLoader;
import mchorse.blockbuster.api.loaders.lazy.ModelLazyLoaderJSON;
import mchorse.blockbuster.api.resource.StreamEntry;
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
            IModelLazyLoader loader = pack.models.get(model);
            ModelCell cell = this.models.get(model);
            long timestamp = loader.lastModified();

            keys.remove(model);

            /* Whether the model should be reloaded */
            if (!force && cell != null && timestamp <= cell.timestamp)
            {
                continue;
            }

            try
            {
                this.addModel(model, loader, timestamp);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        /* Make sure default models don't get reloaded every time,
         * unless substituted */
        Iterator<String> it = keys.iterator();

        while (it.hasNext())
        {
            String key = it.next();
            ModelCell cell = this.models.get(key);

            if (cell.timestamp == 0 && ModelPack.IGNORED_MODELS.contains(key))
            {
                it.remove();
            }
        }

        /* Remove unloaded models */
        for (String key : keys)
        {
            this.removeModel(key);
        }
    }

    /**
     * Add model to the model handler 
     */
    public void addModel(String name, IModelLazyLoader loader, long timestamp) throws Exception
    {
        this.models.put(name, new ModelCell(loader.loadModel(name), timestamp));
    }

    /**
     * Remove model from model handler 
     */
    public void removeModel(String key)
    {
        this.models.remove(key);
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