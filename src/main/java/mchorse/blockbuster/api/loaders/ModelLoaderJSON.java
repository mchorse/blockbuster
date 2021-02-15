package mchorse.blockbuster.api.loaders;

import mchorse.blockbuster.api.loaders.lazy.IModelLazyLoader;
import mchorse.blockbuster.api.loaders.lazy.ModelLazyLoaderJSON;
import mchorse.blockbuster.api.resource.FileEntry;

import java.io.File;

public class ModelLoaderJSON implements IModelLoader
{
    @Override
    public IModelLazyLoader load(File folder)
    {
        File file = new File(folder, "model.json");

        if (file.isFile())
        {
            return new ModelLazyLoaderJSON(new FileEntry(file));
        }

        return null;
    }

}