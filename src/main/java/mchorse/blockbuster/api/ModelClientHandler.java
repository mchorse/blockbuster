package mchorse.blockbuster.api;

import mchorse.blockbuster.api.loaders.lazy.IModelLazyLoader;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelExtrudedLayer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Client-side model handler
 * 
 * The difference between this class and its parent, is that this class 
 * is also compiling {@link ModelCustom} out of added {@link Model}s, 
 * and removes custom models from {@link ModelCustom#MODELS}.
 */
@SideOnly(Side.CLIENT)
public class ModelClientHandler extends ModelHandler
{
    @Override
    @SuppressWarnings("unchecked")
    public void addModel(String name, IModelLazyLoader loader, long timestamp) throws Exception
    {
        super.addModel(name, loader, timestamp);

        ModelCustom.MODELS.put(name, loader.loadClientModel(name, this.models.get(name).model));
    }

    @Override
    public void removeModel(String key)
    {
        super.removeModel(key);

        Minecraft.getMinecraft().addScheduledTask(() ->
        {
            /* If this gets run on the integrated server on the server 
             * side, then it kicks the player due to state exception
             * because OpenGL operations must be done on the client 
             * thread...
             * 
             * Hopefully scheduling it fix this issue 
             */
            ModelCustom model = ModelCustom.MODELS.remove(key);

            if (model != null)
            {
                model.delete();
                ModelExtrudedLayer.clearByModel(model);
            }
        });
    }
}