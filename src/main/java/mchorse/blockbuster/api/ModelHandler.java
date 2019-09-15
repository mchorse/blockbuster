package mchorse.blockbuster.api;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.model.parsing.obj.OBJParser;
import mchorse.blockbuster.client.model.parsing.obj.OBJParser.OBJDataMesh;
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
                InputStream modelStream = entry.customModel.getStream();

                if (modelStream == null)
                {
                    this.addModel(model, new ModelCell(this.generateObjModel(model, entry), timestamp));
                }
                else
                {
                    this.addModel(model, new ModelCell(Model.parse(modelStream), timestamp));
                    modelStream.close();
                }
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

        /* Load default provided models */
        try
        {
            String path = "assets/blockbuster/models/entity/";
            ClassLoader loader = this.getClass().getClassLoader();

            /* Optionally load default models */
            this.addDefaultModel("alex");
            this.addDefaultModel("steve");
            this.addDefaultModel("fred");
            this.addDefaultModel("yike");
            this.addDefaultModel("empty");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Add a default model bundled with the mod 
     */
    private void addDefaultModel(String id) throws Exception
    {
        String path = "assets/blockbuster/models/entity/";
        ClassLoader loader = this.getClass().getClassLoader();

        if (!this.models.containsKey(id))
        {
            this.addModel(id, new ModelCell(Model.parse(loader.getResourceAsStream(path + id + ".json")), 0));
        }
    }

    /**
     * Generate custom model based on given OBJ
     */
    private Model generateObjModel(String model, ModelEntry entry) throws Exception
    {
        /* Generate custom model for an OBJ model */
        InputStream modelStream = this.getClass().getClassLoader().getResourceAsStream("assets/blockbuster/models/entity/obj.json");
        Model data = Model.parse(modelStream);

        data.name = model;

        if (!entry.mtlFile.exists())
        {
            data.providesMtl = false;
        }

        modelStream.close();

        /* Generate limbs */
        OBJParser parser = entry.createOBJParser(model, data);
        boolean remove = true;

        if (parser != null)
        {
            for (OBJDataMesh mesh : parser.objects)
            {
                ModelLimb limb = new ModelLimb();

                limb.name = mesh.name;
                data.limbs.put(mesh.name, limb);

                if (mesh.name.equals("body"))
                {
                    remove = false;
                }
            }
        }

        if (remove)
        {
            data.removeLimb(data.limbs.get("body"));
        }

        data.fillInMissing();

        return data;
    }

    /**
     * Add model to the model handler 
     */
    public void addModel(String name, ModelCell cell)
    {
        this.models.put(name, cell);
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