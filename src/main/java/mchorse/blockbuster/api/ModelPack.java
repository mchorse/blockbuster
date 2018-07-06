package mchorse.blockbuster.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import mchorse.blockbuster.client.model.parsing.obj.OBJParser;

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
     * Cached models
     */
    public Map<String, ModelEntry> models = new HashMap<String, ModelEntry>();

    /**
     * Cached skins
     */
    public Map<String, Map<String, File>> skins = new HashMap<String, Map<String, File>>();

    /**
     * Folders which to check when reloading models and skins
     */
    public List<File> folders = new ArrayList<File>();

    /**
     * List of ignored models
     */
    public static Set<String> IGNORED_MODELS = ImmutableSet.of("steve", "alex", "fred");

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
     * Get available skins for model
     */
    public List<String> getSkins(String model)
    {
        Set<String> keys = this.skins.containsKey(model) ? this.skins.get(model).keySet() : Collections.<String>emptySet();

        return new ArrayList<String>(keys);
    }

    /**
     * Get all available skins
     */
    public Map<String, Map<String, File>> getAllSkins()
    {
        return this.skins;
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
        this.skins.clear();

        for (File folder : this.folders)
        {
            /* TODO: rewrite into one loop */
            this.reloadModels(folder);
            this.reloadSkins(folder);
        }
    }

    /**
     * Reload models
     *
     * Simply caches files in the map for retrieval in actor GUI
     */
    protected void reloadModels(File folder)
    {
        for (File file : folder.listFiles())
        {
            if (file.getName().startsWith("__"))
            {
                continue;
            }

            File model = new File(file.getAbsolutePath() + "/model.json");
            File objModel = new File(file.getAbsolutePath() + "/model.obj");
            File mtlFile = new File(file.getAbsolutePath() + "/model.mtl");

            if (!mtlFile.exists())
            {
                mtlFile = null;
            }

            if (file.isDirectory())
            {
                boolean objExists = objModel.exists();
                boolean modelExists = model.exists();

                if (modelExists || objExists)
                {
                    this.models.put(file.getName(), new ModelEntry(modelExists ? model : null, objExists ? objModel : null, mtlFile));
                }
            }
        }
    }

    /**
     * Reload skins from model folders
     *
     * The algorithm of this method takes the same code from method that above
     * (reloadModels) and scans all skins in the "skins" folder in model's
     * folder.
     */
    protected void reloadSkins(File folder)
    {
        for (File file : folder.listFiles())
        {
            if (file.getName().startsWith("__"))
            {
                continue;
            }

            File skins = new File(file.getAbsolutePath() + "/skins/");

            if (file.isDirectory())
            {
                Map<String, File> map = new HashMap<String, File>();
                String filename = file.getName();

                skins.mkdirs();

                for (File skin : skins.listFiles())
                {
                    int suffix = skin.getName().indexOf(".png");

                    if (suffix != -1)
                    {
                        map.put(skin.getName().substring(0, suffix), skin);
                    }
                }

                if (this.skins.containsKey(filename))
                {
                    this.skins.get(filename).putAll(map);
                }
                else
                {
                    this.skins.put(filename, map);
                }
            }
        }
    }

    /**
     * Model entry 
     */
    public static class ModelEntry
    {
        public File customModel;
        public File objModel;
        public File mtlFile;

        public ModelEntry(File customModel, File objModel, File mtlFile)
        {
            this.customModel = customModel;
            this.objModel = objModel;
            this.mtlFile = mtlFile;
        }

        public long lastModified()
        {
            long a = this.customModel == null ? 0 : this.customModel.lastModified();
            long b = this.objModel == null ? 0 : this.objModel.lastModified();
            long c = this.mtlFile == null ? 0 : this.mtlFile.lastModified();

            return Math.max(Math.max(a, b), c);
        }

        public OBJParser createOBJParser(String key, Model model)
        {
            if (!model.providesObj)
            {
                return null;
            }

            OBJParser parser = new OBJParser(this.objModel, model.providesMtl ? this.mtlFile : null);

            try
            {
                parser.read();
                parser.setupTextures(key, this.objModel.getParentFile());
            }
            catch (Exception e)
            {
                return null;
            }

            return parser;
        }
    }
}