package mchorse.blockbuster.api.loaders.lazy;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.formats.IMeshes;
import mchorse.blockbuster.api.formats.vox.MeshesVOX;
import mchorse.blockbuster.api.formats.vox.Vox;
import mchorse.blockbuster.api.formats.vox.VoxBuilder;
import mchorse.blockbuster.api.formats.vox.VoxReader;
import mchorse.blockbuster.api.resource.IResourceEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

public class ModelLazyLoaderVOX extends ModelLazyLoaderJSON
{
	public IResourceEntry vox;

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
		MeshesVOX meshesVox = new MeshesVOX();

		meshesVox.vox = new VoxReader().read(this.vox.getStream());
		meshesVox.mesh = new VoxBuilder().build(meshesVox.vox);
		meshes.put("vox", meshesVox);

		return meshes;
	}

	/**
	 * Generate custom model based on given VOX
	 */
	private Model generateVOXModel(String model) throws Exception
	{
		/* Generate custom model for a VOX model */
		Model data = new Model();
		ModelPose blocky = new ModelPose();

		data.providesObj = true;
		data.providesMtl = true;
		data.scale[0] = data.scale[1] = data.scale[2] = data.scaleGui = 1 / 16F;
		data.scale[0] *= -1;

		blocky.setSize(1, 1, 1);
		data.poses.put("flying", blocky.clone());
		data.poses.put("standing", blocky.clone());
		data.poses.put("sneaking", blocky.clone());
		data.poses.put("sleeping", blocky.clone());
		data.poses.put("riding", blocky.clone());
		data.name = model;

		/* Generate limbs */
		ModelLimb limb = data.addLimb("vox");
		Vox vox = new VoxReader().read(this.vox.getStream());

		limb.origin[0] = vox.x / 2F;
		limb.origin[2] = vox.z / 2F;

		return data;
	}
}