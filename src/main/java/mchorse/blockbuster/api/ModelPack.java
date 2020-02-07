package mchorse.blockbuster.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import mchorse.blockbuster.api.loaders.IModelLoader;
import mchorse.blockbuster.api.loaders.ModelLoaderJSON;
import mchorse.blockbuster.api.loaders.ModelLoaderOBJ;
import mchorse.blockbuster.api.loaders.ModelLoaderVOX;
import mchorse.blockbuster.api.loaders.lazy.IModelLazyLoader;
import mchorse.blockbuster.api.loaders.lazy.ModelLazyLoaderJSON;
import mchorse.blockbuster.api.resource.StreamEntry;

/**
 * Model pack class
 *
 * Previously was known to be part of ActorsPack, but was decomposed since
 * this code is also required to be on the server side, because the newer
 * code has to collect information about models and skin in save's "blockbuster"
 * folder.
 *
 * This class is responsible for collecting information about models and skins
 * in the given folders. You add which folders to check upon by using
 * {@link #addFolder(String)} method.
 */
public class ModelPack
{
    /**
     * List of ignored models
     */
    public static Set<String> IGNORED_MODELS = ImmutableSet.of("steve", "alex", "fred", "yike", "empty");

    /**
     * List of model loaders
     */
    public List<IModelLoader> loaders = new ArrayList<IModelLoader>();

    /**
     * Cached models
     */
    public Map<String, IModelLazyLoader> models = new HashMap<String, IModelLazyLoader>();

    /**
     * Folders which to check when reloading models and skins
     */
    public List<File> folders = new ArrayList<File>();

    public ModelPack()
    {
        /* TODO: implement reading of loading models from the jar */
        this.loaders.add(new ModelLoaderVOX());
        this.loaders.add(new ModelLoaderOBJ());
        this.loaders.add(new ModelLoaderJSON());
    }

    /**
     * Add a folder to the list of folders to where to look up models and skins
     */
    public void addFolder(String path)
    {
        File folder = new File(path);

        folder.mkdirs();

        if (folder.isDirectory())
        {
            this.folders.add(folder);
        }
    }

    /**
     * Get available models
     */
    public List<String> getModels()
    {
        return new ArrayList<String>(this.models.keySet());
    }

    /**
     * Reload actor resources.
     *
     * Damn, that won't be fun to reload the game every time you want to put
     * another skin in the skins folder, so why not just reload it every time
     * the GUI is showed? It's easy to implement and requires no extra code.
     *
     * This method reloads models from config/blockbuster/models/ and skins from
     * config/blockbuster/models/$model/skins/.
     */
    public void reload()
    {
        this.models.clear();

        for (File folder : this.folders)
        {
            this.reloadModels(folder, "");
        }

        try
        {
            /* Load default provided models */
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
        if (!this.models.containsKey(id))
        {
            String path = "assets/blockbuster/models/entity/";
            ClassLoader loader = this.getClass().getClassLoader();

            this.models.put(id, new ModelLazyLoaderJSON(new StreamEntry(path + id + ".json", 0, loader)));
        }
    }

    /**
     * Reload models
     *
     * Simply caches files in the map for retrieval in actor GUI
     */
    protected void reloadModels(File folder, String prefix)
    {
        for (File file : folder.listFiles())
        {
            String name = file.getName();

            if (name.startsWith("__") || !file.isDirectory())
            {
                continue;
            }

            IModelLazyLoader lazyLoader = null;

            for (IModelLoader loader : this.loaders)
            {
                IModelLazyLoader localLoader = loader.load(file);

                if (localLoader != null)
                {
                    lazyLoader = localLoader;

                    break;
                }
            }

            if (lazyLoader != null)
            {
                this.models.put(prefix + name, lazyLoader);
            }
            else if (!file.getName().equals("skins"))
            {
                this.reloadModels(file, prefix + name + "/");
            }
        }
    }
}