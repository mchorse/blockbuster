package mchorse.blockbuster.api;

import mchorse.blockbuster.Blockbuster;
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
    public void addModel(String key, IModelLazyLoader loader) throws Exception
    {
        super.addModel(key, loader);

        ModelCustom.MODELS.put(key, loader.loadClientModel(key, this.models.get(key)));
    }

    @Override
    protected void addMorph(String key, Model model)
    {
        Blockbuster.proxy.factory.section.add(key, model, true);
    }

    @Override
    public void removeModel(String key)
    {
        super.removeModel(key);

        final ModelCustom model = ModelCustom.MODELS.remove(key);

        if (model == null)
        {
            return;
        }

        Minecraft.getMinecraft().addScheduledTask(() ->
        {
            /* If this gets run on the integrated server on the server 
             * side, then it kicks the player due to state exception
             * because OpenGL operations must be done on the client 
             * thread...
             * 
             * Hopefully scheduling it fix this issue 
             */
            model.delete();
            ModelExtrudedLayer.clearByModel(model);
        });
    }
}