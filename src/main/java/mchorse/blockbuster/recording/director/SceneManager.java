package mchorse.blockbuster.recording.director;

import mchorse.blockbuster.recording.Utils;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SceneManager
{
	/**
	 * Currently loaded scenes
	 */
	public Map<String, Scene> scenes = new HashMap<String, Scene>();

	public void reset()
	{
		this.scenes.clear();
	}

	/**
	 * Tick scenes
	 */
	public void tick()
	{
		Iterator<Scene> it = this.scenes.values().iterator();

		while (it.hasNext())
		{
			Scene scene = it.next();

			scene.tick();

			if (!scene.playing)
			{
				it.remove();
			}
		}
	}

	/**
	 * Play a scene
	 */
	public void play(String filename, World world)
	{
		Scene scene = this.scenes.get(filename);

		if (scene != null)
		{
			return;
		}

		try
		{
			scene = this.load(filename);
		}
		catch (Exception e) {}

		if (scene != null)
		{
			scene.setWorld(world);
			this.scenes.put(filename, scene);
		}
	}

	/**
	 * Load a scene by given filename
	 */
	public Scene load(String filename) throws IOException
	{
		File file = sceneFile(filename);

		if (!file.isFile())
		{
			return null;
		}

		NBTTagCompound compound = CompressedStreamTools.readCompressed(new FileInputStream(file));
		Scene scene = new Scene();

		scene.fromNBT(compound);

		return scene;
	}

	/**
	 * Save a scene by given filename
	 */
	public void save(String filename, Scene scene) throws IOException
	{
		File file = sceneFile(filename);
		NBTTagCompound compound = new NBTTagCompound();

		scene.toNBT(compound);

		CompressedStreamTools.writeCompressed(compound, new FileOutputStream(file));
	}

	/**
	 * Returns a file instance to the scene by given filename
	 */
	private File sceneFile(String filename)
	{
		return Utils.serverFile("blockbuster/scenes", filename);
	}

	/**
	 * Get all the NBT files in the scenes folder
	 */
	public List<String> sceneFiles()
	{
		return Utils.serverFiles("blockbuster/scenes");
	}
}