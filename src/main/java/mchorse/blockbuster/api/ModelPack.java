package mchorse.blockbuster.api;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.api.loaders.IModelLoader;
import mchorse.blockbuster.api.loaders.ModelLoaderJSON;
import mchorse.blockbuster.api.loaders.ModelLoaderOBJ;
import mchorse.blockbuster.api.loaders.ModelLoaderVOX;
import mchorse.blockbuster.api.loaders.lazy.IModelLazyLoader;
import mchorse.blockbuster.api.loaders.lazy.ModelLazyLoaderJSON;
import mchorse.blockbuster.api.resource.StreamEntry;
import net.minecraftforge.common.DimensionManager;

/**
 * Model pack class
 */
public class ModelPack
{
    /**
     * List of model loaders
     */
    public List<IModelLoader> loaders = new ArrayList<IModelLoader>();

    /**
     * Cached models
     */
    public Map<String, IModelLazyLoader> models = new HashMap<String, IModelLazyLoader>();

    /**
     * Folders which to check when reloading models
     */
    public List<File> folders = new ArrayList<File>();

    /**
     * Map for only changed models
     */
    public Map<String, IModelLazyLoader> changed = new HashMap<String, IModelLazyLoader>();

    /**
     * List of removed models
     */
    public List<String> removed = new ArrayList<String>();

    private long lastTime;

    public ModelPack()
    {
        this.loaders.add(new ModelLoaderVOX());
        this.loaders.add(new ModelLoaderOBJ());
        this.loaders.add(new ModelLoaderJSON());

        this.setupFolders();
    }

    public IModelLazyLoader create(File file)
    {
        IModelLazyLoader lazyLoader = null;

        for (IModelLoader loader : this.loaders)
        {
            lazyLoader = loader.load(file);

            if (lazyLoader != null)
            {
                break;
            }
        }

        return lazyLoader;
    }

    /**
     * Setup folders
     */
    public void setupFolders()
    {
        this.folders.clear();
        this.addFolder(new File(CommonProxy.configFile, "models"));

        if (Blockbuster.modelFolderPath != null && !Blockbuster.modelFolderPath.get().isEmpty())
        {
            this.addFolder(new File(Blockbuster.modelFolderPath.get()));
        }

        File server = DimensionManager.getCurrentSaveRootDirectory();

        if (server != null)
        {
            this.addFolder(new File(server, "blockbuster/models"));
        }
    }

    /**
     * Add a folder to the list of folders to where to look up models and skins
     */
    private void addFolder(File folder)
    {
        folder.mkdirs();

        if (folder.isDirectory())
        {
            this.folders.add(folder);
        }
    }

    /**
     * Reload model handler
     */
    public void reload()
    {
        this.setupFolders();

        this.changed.clear();
        this.removed.clear();
        this.lastTime = System.currentTimeMillis();

        for (File folder : this.folders)
        {
            this.reloadModels(folder, "");
        }

        this.removeOld();

        try
        {
            /* Load default provided models */
            this.addDefaultModel("alex");
            this.addDefaultModel("alex_3d");
            this.addDefaultModel("steve");
            this.addDefaultModel("steve_3d");
            this.addDefaultModel("fred");
            this.addDefaultModel("fred_3d");
            this.addDefaultModel("empty");
            this.addDefaultModel("cape");

            /* Eyes related models */
            this.addDefaultModel("eyes/3.0");
            this.addDefaultModel("eyes/3.0_1px");
            this.addDefaultModel("eyes/alex_eyes");
            this.addDefaultModel("eyes/fred_eyes");
            this.addDefaultModel("eyes/steve_eyes");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Remove old entries
     */
    private void removeOld()
    {
        Iterator<Map.Entry<String, IModelLazyLoader>> it = this.models.entrySet().iterator();

        while (it.hasNext())
        {
            Map.Entry<String, IModelLazyLoader> entry = it.next();
            long lastTime = entry.getValue().getLastTime();

            if (lastTime < this.lastTime && lastTime >= 0)
            {
                it.remove();

                this.removed.add(entry.getKey());
            }
        }
    }

    /**
     * Add a default model bundled with the mod
     */
    private void addDefaultModel(String id) throws Exception
    {
        IModelLazyLoader lazy = this.models.get(id);

        if (lazy == null)
        {
            String path = "assets/blockbuster/models/entity/";
            ClassLoader loader = this.getClass().getClassLoader();

            lazy = new ModelLazyLoaderJSON(new StreamEntry(path + id + ".json", 0, loader));
            lazy.setLastTime(-1);

            this.models.put(id, lazy);
            this.changed.put(id, lazy);
            this.removed.remove(id);
        }
    }

    /**
     * Reload models
     *
     * Simply caches files in the map
     */
    protected void reloadModels(File folder, String prefix)
    {
        for (File file : folder.listFiles())
        {
            String name = file.getName();

            if (name.startsWith("__") || name.equals("skins") || file.isFile() || (name.equals("particles") && prefix.isEmpty()))
            {
                continue;
            }

            String path = prefix + name;
            IModelLazyLoader lazyLoader = this.models.get(path);

            if (lazyLoader != null && lazyLoader.getLastTime() >= 0)
            {
                if (lazyLoader.stillExists())
                {
                    lazyLoader.setLastTime(this.lastTime);

                    if (lazyLoader.hasChanged())
                    {
                        this.changed.put(path, lazyLoader);
                    }

                    continue;
                }
            }
            else
            {
                /* Overwriting the default model */
                lazyLoader = null;
            }

            for (IModelLoader loader : this.loaders)
            {
                lazyLoader = loader.load(file);

                if (lazyLoader != null)
                {
                    lazyLoader.setLastTime(this.lastTime);

                    break;
                }
            }

            if (lazyLoader != null)
            {
                this.models.put(path, lazyLoader);
                this.changed.put(path, lazyLoader);
            }
            else
            {
                this.reloadModels(file, path + "/");
            }
        }
    }
}