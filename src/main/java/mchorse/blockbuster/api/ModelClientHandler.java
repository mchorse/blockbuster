package mchorse.blockbuster.api;

import java.io.File;

import mchorse.blockbuster.api.ModelPack.ModelEntry;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelParser;

/**
 * Client-side model handler
 * 
 * The difference between this class and its parent, is that this class 
 * is also compiling {@link ModelCustom} out of added {@link Model}s.
 */
public class ModelClientHandler extends ModelHandler
{
    @Override
    @SuppressWarnings("unchecked")
    protected void addModel(String name, ModelCell cell)
    {
        super.addModel(name, cell);

        ModelEntry entry = this.pack.models.get(name);
        Model mod = cell.model;

        File objModel = null;
        boolean fallback = true;

        if (this.pack.models.containsKey(name))
        {
            objModel = entry.objModel;
        }

        if (!mod.model.isEmpty())
        {
            try
            {
                Class<? extends ModelCustom> clazz = (Class<? extends ModelCustom>) Class.forName(mod.model);

                /* Parse custom custom model with a custom class */
                ModelParser.parse(name, mod, clazz, objModel);

                fallback = false;
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        if (fallback)
        {
            ModelParser.parse(name, mod, objModel);
        }
    }
}