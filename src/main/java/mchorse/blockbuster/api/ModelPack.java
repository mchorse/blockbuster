package mchorse.blockbuster.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

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
    public static Set<String> IGNORED_MODELS = ImmutableSet.of("steve", "alex", "fred", "yike");

    /**
     * Cached models
     */
    public Map<String, ModelEntry> models = new HashMap<String, ModelEntry>();

    /**
     * Folders which to check when reloading models and skins
     */
    public List<File> folders = new ArrayList<File>();

    public ModelPack()
    {
        /* TODO: implement reading of loading models from the jar */
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

            File model = new File(file.getAbsolutePath() + "/model.json");
            File objModel = new File(file.getAbsolutePath() + "/model.obj");
            File mtlFile = new File(file.getAbsolutePath() + "/model.mtl");

            boolean mtlExists = mtlFile.exists();
            boolean objExists = objModel.exists();
            boolean modelExists = model.exists();

            if (!mtlExists)
            {
                mtlFile = null;
            }

            if (modelExists || objExists)
            {
                this.models.put(prefix + name, new ModelEntry(modelExists ? model : null, objExists ? objModel : null, mtlFile));
            }
            else if (!file.getName().equals("skins"))
            {
                this.reloadModels(file, prefix + name + "/");
            }
        }
    }
}