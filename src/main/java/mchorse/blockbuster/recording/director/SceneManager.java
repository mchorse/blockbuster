package mchorse.blockbuster.recording.director;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.recording.Utils;
import mchorse.blockbuster.recording.data.Mode;
import net.minecraft.entity.player.EntityPlayerMP;
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
import java.util.regex.Pattern;

/**
 * Scene manager
 *
 * This bro allows to manage scenes (those are something like remote director blocks).
 */
public class SceneManager
{
	public static final Pattern FILENAME = Pattern.compile("^[^<>:;,?\"*|/]+$");

	/**
	 * Currently loaded scenes
	 */
	public Map<String, Scene> scenes = new HashMap<String, Scene>();

	public static boolean isValidFilename(String filename)
	{
		return !filename.isEmpty() && FILENAME.matcher(filename).matches();
	}

	/**
	 * Reset scene manager
	 */
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
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (scene != null)
		{
			scene.setWorld(world);
			scene.startPlayback(0);

			this.scenes.put(filename, scene);
		}
	}

	/**
	 * Record the player
	 */
	public void record(String filename, String record, EntityPlayerMP player)
	{
		final Scene scene = this.get(filename, player.worldObj);

		if (scene != null)
		{
			scene.setWorld(player.worldObj);

			final Replay replay = scene.getByFile(record);

			if (replay != null)
			{
				CommonProxy.manager.record(replay.id, player, Mode.ACTIONS, replay.teleportBack, true, () ->
				{
					if (!CommonProxy.manager.recorders.containsKey(player))
					{
						this.scenes.put(filename, scene);
						scene.startPlayback(record);
					}
					else
					{
						scene.stopPlayback();
					}

					replay.apply(player);
				});
			}
		}
	}

	/**
	 * Toggle playback of a scene by given filename
	 */
	public void toggle(String filename, World world)
	{
		Scene scene = this.scenes.get(filename);

		if (scene != null)
		{
			scene.stopPlayback();
		}
		else
		{
			this.play(filename, world);
		}
	}

	/**
	 * Get currently running or load a scene
	 */
	public Scene get(String filename, World world)
	{
		Scene scene = this.scenes.get(filename);

		if (scene != null)
		{
			return scene;
		}

		try
		{
			scene = this.load(filename);

			if (scene != null)
			{
				scene.setWorld(world);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return scene;
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

		scene.setId(filename);
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
	 * Rename a scene on the disk
	 */
	public boolean rename(String from, String to)
	{
		File fromFile = sceneFile(from);
		File toFile = sceneFile(to);

		if (fromFile.isFile() && !toFile.exists())
		{
			return fromFile.renameTo(toFile);
		}

		return false;
	}

	/**
	 * Remove a scene from the disk
	 */
	public boolean remove(String filename)
	{
		File file = sceneFile(filename);

		if (file.exists())
		{
			return file.delete();
		}

		return false;
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