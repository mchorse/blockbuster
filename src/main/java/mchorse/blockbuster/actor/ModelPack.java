package mchorse.blockbuster.actor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModelPack
{
    /**
     * Cached models
     */
    public Map<String, File> models = new HashMap<String, File>();

    /**
     * Cached skins
     */
    public Map<String, Map<String, File>> skins = new HashMap<String, Map<String, File>>();

    /**
     * Folders which to check when collecting all models and skins
     */
    public List<File> folders = new ArrayList<File>();

    /**
     * Add a folder to the list of folders to where to look up models and skins
     */
    public void addFolder(String path)
    {
        File folder = new File(path);

        folder.mkdirs();

        if (folder.isDirectory()) this.folders.add(folder);
    }

    /**
     * Get available skins for model
     */
    public List<String> getSkins(String model)
    {
        Set<String> keys = this.skins.containsKey(model) ? this.skins.get(model).keySet() : Collections.<String> emptySet();

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
     * Reload actor resources
     *
     * Damn, that won't be fun to reload the game every time you want to put
     * another skin in the skins folder, so why not just reload it every time
     * the GUI is showed? It's easy to implement and requires no extra code.
     *
     * This method reloads models from config/blockbuster/models/ and skins from
     * config/blockbuster/models/$model/skins/
     */
    public void reload()
    {
        this.models.clear();
        this.skins.clear();

        for (File folder : this.folders)
        {
            this.reloadModels(folder);
            this.reloadSkins(folder);
        }
    }

    /**
     * Reload models
     *
     * Simply caches file instances in the map for retrieval in actor GUI
     */
    protected void reloadModels(File folder)
    {
        for (File file : folder.listFiles())
        {
            File model = new File(file.getAbsolutePath() + "/model.json");

            if (file.isDirectory() && model.isFile())
            {
                this.models.put(file.getName(), model);
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
            File skins = new File(file.getAbsolutePath() + "/skins/");

            if (file.isDirectory())
            {
                Map<String, File> map = new HashMap<String, File>();

                skins.mkdirs();

                for (File skin : skins.listFiles())
                {
                    int suffix = skin.getName().indexOf(".png");

                    if (suffix != -1)
                    {
                        map.put(skin.getName().substring(0, suffix), skin);
                    }
                }

                this.skins.put(file.getName(), map);
            }
        }
    }
}
