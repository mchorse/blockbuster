package mchorse.blockbuster.api.loaders.lazy;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.api.formats.IMeshes;
import mchorse.blockbuster.api.formats.vox.MeshesVOX;
import mchorse.blockbuster.api.formats.vox.VoxDocument;
import mchorse.blockbuster.api.formats.vox.VoxReader;
import mchorse.blockbuster.api.formats.vox.data.Vox;
import mchorse.blockbuster.api.resource.IResourceEntry;
import mchorse.blockbuster.client.model.ModelCustom;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

public class ModelLazyLoaderVOX extends ModelLazyLoaderJSON
{
	public IResourceEntry vox;

	private VoxDocument cachedDocument;

	public ModelLazyLoaderVOX(IResourceEntry model, IResourceEntry vox)
	{
		super(model);

		this.vox = vox;
	}

	@Override
	public int getFilenameHash()
	{
		return (this.model.getName() + "/" + this.vox.getName()).hashCode();
	}

	@Override
	public long lastModified()
	{
		return Math.max(this.model.lastModified(), this.vox.lastModified());
	}

	@Override
	public Model loadModel(String key) throws Exception
	{
		Model model = null;

		try
		{
			model = super.loadModel(key);
		}
		catch (Exception e) {}

		if (model == null)
		{
			model = this.generateVOXModel(key);
		}

		return model;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected Map<String, IMeshes> getMeshes(String key, Model model) throws Exception
	{
		Map<String, IMeshes> meshes = new HashMap<String, IMeshes>();
		VoxDocument document = this.getVox();

		for (VoxDocument.LimbNode node : document.generate())
		{
			meshes.put(node.name, new MeshesVOX(document, node));
		}

		return meshes;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelCustom loadClientModel(String key, Model model) throws Exception
	{
		ModelCustom custom = super.loadClientModel(key, model);

		this.cachedDocument = null;

		return custom;
	}

	/**
	 * Generate custom model based on given VOX
	 */
	private Model generateVOXModel(String model) throws Exception
	{
		/* Generate custom model for a VOX model */
		Model data = new Model();
		ModelPose blocky = new ModelPose();

		/* Generate limbs */
		VoxDocument document = this.getVox();

		for (VoxDocument.LimbNode node : document.generate())
		{
			ModelLimb limb = data.addLimb(node.name);
			ModelTransform transform = new ModelTransform();

			limb.origin[0] = 0;
			limb.origin[1] = 0;
			limb.origin[2] = 0;

			transform.translate[0] = -node.translation.x;
			transform.translate[1] = node.translation.z;
			transform.translate[2] = -node.translation.y;

			blocky.limbs.put(limb.name, transform);
		}

		/* General model properties */
		data.providesObj = true;
		data.providesMtl = true;

		blocky.setSize(1, 1, 1);
		data.poses.put("flying", blocky.clone());
		data.poses.put("standing", blocky.clone());
		data.poses.put("sneaking", blocky.clone());
		data.poses.put("sleeping", blocky.clone());
		data.poses.put("riding", blocky.clone());
		data.name = model;

		return data;
	}

	private VoxDocument getVox() throws Exception
	{
		if (this.cachedDocument != null)
		{
			return this.cachedDocument;
		}

		return this.cachedDocument = new VoxReader().read(this.vox.getStream());
	}
}