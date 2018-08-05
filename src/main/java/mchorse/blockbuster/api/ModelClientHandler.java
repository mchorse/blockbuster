package mchorse.blockbuster.api;

import java.util.Map;

import mchorse.blockbuster.api.ModelPack.ModelEntry;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelExtrudedLayer;
import mchorse.blockbuster.client.model.parsing.ModelParser;
import mchorse.blockbuster.client.model.parsing.obj.OBJParser;
import mchorse.blockbuster.client.model.parsing.obj.OBJParser.MeshObject;
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
    public void addModel(String name, ModelCell cell)
    {
        super.addModel(name, cell);

        ModelEntry entry = this.pack.models.get(name);
        Model mod = cell.model;

        OBJParser parser = entry == null ? null : entry.createOBJParser(name, mod);
        Map<String, MeshObject> meshes = parser == null ? null : parser.compile();
        boolean fallback = true;

        if (!mod.model.isEmpty())
        {
            try
            {
                Class<? extends ModelCustom> clazz = (Class<? extends ModelCustom>) Class.forName(mod.model);

                /* GC the old model */
                ModelExtrudedLayer.clearByModel(ModelCustom.MODELS.get(name));

                /* Parse custom custom model with a custom class */
                ModelParser.parse(name, mod, clazz, meshes);

                fallback = false;
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        if (fallback)
        {
            ModelParser.parse(name, mod, meshes);
        }
    }

    @Override
    public void removeModel(String key)
    {
        super.removeModel(key);

        ModelCustom model = ModelCustom.MODELS.remove(key);

        model.delete();
        ModelExtrudedLayer.clearByModel(model);
    }
}