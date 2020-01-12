package mchorse.blockbuster.api.loaders.lazy;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.formats.IMeshes;
import mchorse.blockbuster.api.formats.obj.MeshesOBJ;
import mchorse.blockbuster.api.resource.IResourceEntry;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelExtrudedLayer;
import mchorse.blockbuster.client.model.parsing.ModelParser;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

public class ModelLazyLoaderJSON implements IModelLazyLoader
{
	public IResourceEntry model;

	public ModelLazyLoaderJSON(IResourceEntry model)
	{
		this.model = model;
	}

	@Override
	public long lastModified()
	{
		return this.model.lastModified();
	}

	@Override
	public Model loadModel(String key) throws Exception
	{
		if (!this.model.exists())
		{
			return null;
		}

		return Model.parse(this.model.getStream());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelCustom loadClientModel(String key, Model model) throws Exception
	{
		/* GC the old model */
		ModelExtrudedLayer.clearByModel(ModelCustom.MODELS.get(key));
		Map<String, IMeshes> meshes = this.getMeshes(key, model);

		if (!model.model.isEmpty())
		{
			try
			{
				Class<? extends ModelCustom> clazz = (Class<? extends ModelCustom>) Class.forName(model.model);

				/* Parse custom custom model with a custom class */
				return ModelParser.parse(key, model, clazz, meshes);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}

		return ModelParser.parse(key, model, meshes);
	}

	@SideOnly(Side.CLIENT)
	protected Map<String, IMeshes> getMeshes(String key, Model model) throws Exception
	{
		return null;
	}
}
