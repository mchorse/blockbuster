package mchorse.blockbuster.api.loaders.lazy;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.api.formats.IMeshes;
import mchorse.blockbuster.api.formats.obj.OBJDataMesh;
import mchorse.blockbuster.api.resource.FileEntry;
import mchorse.blockbuster.api.resource.IResourceEntry;
import mchorse.blockbuster.api.formats.obj.OBJParser;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.StringJoiner;

public class ModelLazyLoaderOBJ extends ModelLazyLoaderJSON
{
	public IResourceEntry obj;
	public IResourceEntry mtl;

	private OBJParser parser;
	private long lastModified;

	public ModelLazyLoaderOBJ(IResourceEntry model, IResourceEntry obj, IResourceEntry mtl)
	{
		super(model);

		this.obj = obj;
		this.mtl = mtl;
	}

	@Override
	public int getFilenameHash()
	{
		return (this.getName(this.model) + ":" + this.getName(this.obj) + ":" + this.getName(this.mtl)).hashCode();
	}

	@Override
	public long lastModified()
	{
		return Math.max(this.model.lastModified(), Math.max(this.obj.lastModified(), this.mtl.lastModified()));
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
			model = this.generateOBJModel(key);
		}

		return model;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected Map<String, IMeshes> getMeshes(String key, Model model) throws Exception
	{
		try
		{
			Map<String, IMeshes> meshes = this.getOBJParser(key, model).compile();

			this.parser = null;
			this.lastModified = 0;

			return meshes;
		}
		catch (Exception e) {}

		return null;
	}

	/**
	 * Create an OBJ parser
	 */
	public OBJParser getOBJParser(String key, Model model)
	{
		if (!model.providesObj)
		{
			return null;
		}

		long lastModified = this.lastModified();

		if (this.lastModified < lastModified)
		{
			this.lastModified = lastModified;
		}
		else
		{
			return this.parser;
		}

		try
		{
			InputStream obj = this.obj.getStream();
			InputStream mtl = model.providesMtl ? this.mtl.getStream() : null;

			this.parser = new OBJParser(obj, mtl);
			this.parser.read();

			if (this.mtl instanceof FileEntry)
			{
				this.parser.setupTextures(key, ((FileEntry) this.mtl).file.getParentFile());
			}

			model.materials.putAll(this.parser.materials);
		}
		catch (Exception e)
		{
			return null;
		}

		return this.parser;
	}

	/**
	 * Generate custom model based on given OBJ
	 */
	private Model generateOBJModel(String model)
	{
		/* Generate custom model for an OBJ model */
		Model data = new Model();
		ModelPose blocky = new ModelPose();

		blocky.setSize(1, 1, 1);
		data.poses.put("flying", blocky.clone());
		data.poses.put("standing", blocky.clone());
		data.poses.put("sneaking", blocky.clone());
		data.poses.put("sleeping", blocky.clone());
		data.poses.put("riding", blocky.clone());
		data.name = model;

		data.providesObj = true;
		data.providesMtl = this.mtl.exists();

		/* Generate limbs */
		OBJParser parser = this.getOBJParser(model, data);

		if (parser != null)
		{
			for (OBJDataMesh mesh : parser.objects)
			{
				data.addLimb(mesh.name);
			}
		}

		if (data.limbs.isEmpty())
		{
			data.addLimb("body");
		}

		/* Flip around the X axis */
		for (ModelPose pose : data.poses.values())
		{
			for (ModelTransform transform : pose.limbs.values())
			{
				transform.scale[0] *= -1;
			}
		}

		return data;
	}

	@Override
	public boolean copyFiles(File folder)
	{
		boolean skins = super.copyFiles(folder);
		boolean obj = this.obj.copyTo(new File(folder, this.obj.getName()));
		boolean mtl = this.mtl.copyTo(new File(folder, this.mtl.getName()));

		return skins || obj || mtl;
	}
}