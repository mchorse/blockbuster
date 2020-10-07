package mchorse.blockbuster.client;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

public class SkinHandler
{
	/**
	 * Check general skins folder, and move these files to appropriate
	 * skins folders
	 */
	public static void checkSkinsFolder()
	{
		if (!ClientProxy.skinsFolder.exists())
		{
			return;
		}

		File[] files = ClientProxy.skinsFolder.listFiles();

		if (files == null)
		{
			return;
		}

		for (File file : files)
		{
			if (file.isFile())
			{
				tryReadingMovingSkinFile(file);
			}
		}
	}

	private static void tryReadingMovingSkinFile(File file)
	{
		try
		{
			FileInputStream stream = new FileInputStream(file);

			try (ImageInputStream in = ImageIO.createImageInputStream(stream))
			{
				final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);

				if (readers.hasNext())
				{
					ImageReader reader = readers.next();

					try
					{
						reader.setInput(in);
						int w = reader.getWidth(0);
						int h = reader.getHeight(0);
						reader.dispose();
						in.close();
						stream.close();

						tryMovingSkin(file, w, h);
					}
					finally
					{
						reader.dispose();
					}

					in.close();
					stream.close();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void tryMovingSkin(File file, int w, int h)
	{
		float aspect = w / (float) h;

		/* Must be 1 to 1 or 2 to 1 aspect ratio */
		if (!(aspect == 2F || aspect == 1F))
		{
			return;
		}

		int hf = aspect == 1F ? 64 : 32;

		if (!(w % 64 == 0 || h % hf == 0))
		{
			return;
		}

		moveToDestination(hf == 64 ? "fred" : "steve", file);
	}

	private static void moveToDestination(String folder, File input)
	{
		String name = input.getName();
		File file = new File(CommonProxy.configFile, "models/" + folder + "/skins/" + name);

		for (int i = 0; file.exists() && i < 1000; i++)
		{
			file = new File(CommonProxy.configFile, "models/" + folder + "/skins/" + FilenameUtils.getBaseName(name) + "_" + i + FilenameUtils.getExtension(name));

			if (i >= 1000)
			{
				return;
			}

			i++;
		}

		if (!file.exists() && input.renameTo(file))
		{
			L10n.success(Minecraft.getMinecraft().player, "model.skin_moved", name, "blockbuster." + folder);
		}
	}
}