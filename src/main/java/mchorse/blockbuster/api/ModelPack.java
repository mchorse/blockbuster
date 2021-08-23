package mchorse.blockbuster.api;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.api.loaders.IModelLoader;
import mchorse.blockbuster.api.loaders.ModelLoaderJSON;
import mchorse.blockbuster.api.loaders.ModelLoaderOBJ;
import mchorse.blockbuster.api.loaders.ModelLoaderVOX;
import mchorse.blockbuster.api.loaders.lazy.IModelLazyLoader;
import mchorse.blockbuster.api.loaders.lazy.ModelLazyLoaderJSON;
import mchorse.blockbuster.api.loaders.lazy.ModelLazyLoaderOBJ;
import mchorse.blockbuster.api.loaders.lazy.ModelLazyLoaderVOX;
import mchorse.blockbuster.api.resource.IResourceEntry;
import mchorse.blockbuster.api.resource.StreamEntry;
import mchorse.blockbuster.utils.mclib.ImageFolder;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    private Map<String, ModelUserItem> packed = new HashMap<String, ModelUserItem>();

    public ModelPack()
    {
        this.loaders.add(new ModelLoaderVOX());
        this.loaders.add(new ModelLoaderOBJ());
        this.loaders.add(new ModelLoaderJSON());

        this.setupFolders();
        this.setupPackedModels();
    }

    private void setupPackedModels()
    {
        try
        {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("assets/blockbuster/models/user.json");
            String json = IOUtils.toString(stream, StandardCharsets.UTF_8);

            this.packed = new Gson().fromJson(json, new TypeToken<Map<String, ModelUserItem>>(){}.getType());
        }
        catch (Exception e)
        {}
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
        this.addFolder(new ImageFolder(CommonProxy.configFile, "models"));

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
            List<String> shapes = ImmutableList.of(
                "eyebrow_l",
                "eyebrow_r",
                "eyelid_lb",
                "eyelid_lt",
                "eyelid_rb",
                "eyelid_rt"
            );

            this.addDefaultModel("eyes/3.0");
            this.addDefaultModel("eyes/3.0_1px");
            this.addDefaultModelWithShapes("eyes/3.1", shapes);
            this.addDefaultModelWithShapes("eyes/3.1_simple", shapes);
            this.addDefaultModel("eyes/alex");
            this.addDefaultModel("eyes/steve");
            this.addDefaultModel("eyes/fred");
            this.addDefaultModel("eyes/head");
            this.addDefaultModel("eyes/head_3d");

            /* Of course I know him, he's me */
            this.addDefaultModel("mchorse/head");

            if (this.packed != null)
            {
                for (Map.Entry<String, ModelUserItem> entry : this.packed.entrySet())
                {
                    this.addUserModel(entry.getKey(), entry.getValue());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void addUserModel(String id, ModelUserItem userItem)
    {
        IModelLazyLoader lazy = this.models.get(id);

        if (lazy == null)
        {
            String path = "assets/blockbuster/models/user/" + id;
            ClassLoader loader = this.getClass().getClassLoader();

            StreamEntry json = new StreamEntry(path + "/model.json", 0, loader);

            if (userItem.obj != null)
            {
                String mtlPath = userItem.mtl == null ? null : path + "/" + userItem.mtl;
                StreamEntry obj = new StreamEntry(path + "/" + userItem.obj, 0, loader);
                StreamEntry mtl = new StreamEntry(mtlPath, 0, loader);
                List<IResourceEntry> s = new ArrayList<IResourceEntry>();

                if (userItem.shapes != null)
                {
                    for (String shape : userItem.shapes)
                    {
                        s.add(new StreamEntry(path + "/"  + shape, 0, loader));
                    }
                }

                lazy = new ModelLazyLoaderOBJ(json, obj, mtl, s);
            }
            else if (userItem.vox != null)
            {
                lazy = new ModelLazyLoaderVOX(json, new StreamEntry(path + "/" + userItem.vox, 0, loader));
            }
            else
            {
                lazy = new ModelLazyLoaderJSON(json);
            }

            lazy.setLastTime(-1);

            this.models.put(id, lazy);
            this.changed.put(id, lazy);
            this.removed.remove(id);
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

    private void addDefaultModelWithShapes(String id, List<String> shapes) throws Exception
    {
        IModelLazyLoader lazy = this.models.get(id);

        if (lazy == null)
        {
            String path = "assets/blockbuster/models/entity/";
            ClassLoader loader = this.getClass().getClassLoader();

            StreamEntry json = new StreamEntry(path + id + ".json", 0, loader);
            StreamEntry obj = new StreamEntry(path + id + "/base.obj", 0, loader);
            List<IResourceEntry> s = new ArrayList<IResourceEntry>();

            for (String shape : shapes)
            {
                s.add(new StreamEntry(path + id + "/"  + shape + ".obj", 0, loader));
            }

            lazy = new ModelLazyLoaderOBJ(json, obj, new StreamEntry(null, 0, loader), s);
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