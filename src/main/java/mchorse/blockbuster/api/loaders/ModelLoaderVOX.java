package mchorse.blockbuster.api.loaders;

import mchorse.blockbuster.api.loaders.lazy.IModelLazyLoader;
import mchorse.blockbuster.api.loaders.lazy.ModelLazyLoaderVOX;
import mchorse.blockbuster.api.resource.FileEntry;
import mchorse.blockbuster.api.resource.IResourceEntry;

import java.io.File;

public class ModelLoaderVOX implements IModelLoader
{
    @Override
    public IModelLazyLoader load(File folder)
    {
        IResourceEntry json = new FileEntry(new File(folder, "model.json"));
        File vox = new File(folder, "model.vox");

        if (vox.isFile())
        {
            return new ModelLazyLoaderVOX(json, new FileEntry(vox));
        }

        for (File file : folder.listFiles())
        {
            if (file.isFile() && file.getName().endsWith(".vox"))
            {
                return new ModelLazyLoaderVOX(json, new FileEntry(file));
            }
        }

        return null;
    }
}