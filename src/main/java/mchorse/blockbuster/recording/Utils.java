package mchorse.blockbuster.recording;

import net.minecraftforge.common.DimensionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Utils
{
    /**
	 * Get path to server file in given folder
	 */
	public static File serverFile(String folder, String filename)
	{
		File file = new File(DimensionManager.getCurrentSaveRootDirectory() + "/" + folder);

		if (!file.exists())
		{
			file.mkdirs();
		}

		return new File(file, filename + ".dat");
	}

	/**
	 * Get list of all available replays
	 */
	public static List<String> serverFiles(String folder)
	{
		List<String> list = new ArrayList<String>();
		File replays = new File(DimensionManager.getCurrentSaveRootDirectory() + "/" + folder);
		File[] files = replays.listFiles();

		if (files == null)
		{
			return list;
		}

		for (File file : files)
		{
			String name = file.getName();

			if (file.isFile() && name.endsWith(".dat"))
			{
				int index = name.lastIndexOf(".");

				list.add(name.substring(0, index));
			}
		}

		return list;
	}
}