package mchorse.blockbuster.utils;

import java.io.File;

public class TextureUtils
{
	public static File getFirstAvailableFile(File folder, String name)
	{
		File file = new File(folder, name + ".png");
		int index = 0;

		while (file.exists())
		{
			index += 1;
			file = new File(folder, name + index + ".png");
		}

		return file;
	}
}
