package mchorse.blockbuster.api.loaders;

import mchorse.blockbuster.api.loaders.lazy.IModelLazyLoader;
import mchorse.blockbuster.api.loaders.lazy.ModelLazyLoaderOBJ;
import mchorse.blockbuster.api.resource.FileEntry;
import mchorse.blockbuster.api.resource.IResourceEntry;

import java.io.File;

public class ModelLoaderOBJ implements IModelLoader
{
	@Override
	public IModelLazyLoader load(File folder)
	{
		IResourceEntry json = new FileEntry(new File(folder, "model.json"));
		File obj = new File(folder, "model.obj");
		File shapes = new File(folder, "shapes");

		if (obj.isFile())
		{
			File mtl = new File(folder, "model.mtl");

			return new ModelLazyLoaderOBJ(json, new FileEntry(obj), new FileEntry(mtl), shapes);
		}

		for (File file : folder.listFiles())
		{
			if (file.isFile() && file.getName().endsWith(".obj"))
			{
				String name = file.getName();
				File mtl = new File(folder, name.substring(0, name.length() - 3) + "mtl");

				return new ModelLazyLoaderOBJ(json, new FileEntry(file), new FileEntry(mtl), shapes);
			}
		}

		return null;
	}
}