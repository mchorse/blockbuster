package mchorse.blockbuster.api;

import java.io.File;

import mchorse.blockbuster.api.ModelPack.ModelEntry;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelExtrudedLayer;
import mchorse.blockbuster.client.model.parsing.ModelParser;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Client-side model handler
 * 
 * The difference between this class and its parent, is that this class 
 * is also compiling {@link ModelCustom} out of added {@link Model}s, 
 * and removes custom models from {@value ModelCustom#MODELS}.  
 */
@SideOnly(Side.CLIENT)
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
        File mtlFile = null;
        boolean fallback = true;

        if (entry != null)
        {
            objModel = entry.objModel;
            mtlFile = entry.mtlFile;
        }

        if (!mod.model.isEmpty())
        {
            try
            {
                Class<? extends ModelCustom> clazz = (Class<? extends ModelCustom>) Class.forName(mod.model);

                /* GC the old model */
                ModelExtrudedLayer.clearByModel(ModelCustom.MODELS.get(name));

                /* Parse custom custom model with a custom class */
                ModelParser.parse(name, mod, clazz, objModel, mtlFile);

                fallback = false;
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        if (fallback)
        {
            ModelParser.parse(name, mod, objModel, mtlFile);
        }
    }

    @Override
    protected void removeModel(String key)
    {
        super.removeModel(key);

        ModelExtrudedLayer.clearByModel(ModelCustom.MODELS.remove(key));
    }
}