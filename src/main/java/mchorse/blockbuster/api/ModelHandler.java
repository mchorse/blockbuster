package mchorse.blockbuster.api;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.loaders.lazy.IModelLazyLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

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
    public void loadModels(ModelPack pack, boolean force)
    {
        pack.reload();

        /* Load user provided models */
        for (Map.Entry<String, IModelLazyLoader> entry : (force ? pack.models.entrySet() : pack.changed.entrySet()))
        {
            IModelLazyLoader loader = entry.getValue();

            try
            {
                if (force)
                {
                    this.removeModel(entry.getKey());
                }

                this.addModel(entry.getKey(), loader);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("Error happened with " + entry.getKey());
            }
        }

        /* Remove unloaded models */
        for (String key : pack.removed)
        {
            this.removeModel(key);
        }
    }

    /**
     * Add model to the model handler 
     */
    public void addModel(String key, IModelLazyLoader loader) throws Exception
    {
        Model model = loader.loadModel(key);

        this.models.put(key, model);

        Blockbuster.proxy.factory.section.add(key, model);
    }

    /**
     * Remove model from model handler 
     */
    public void removeModel(String key)
    {
        Model model = this.models.remove(key);

        if (model != null)
        {
            Blockbuster.proxy.factory.section.remove(key);
        }
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
        Blockbuster.proxy.loadModels(false);
    }
}