package mchorse.blockbuster.client.particles;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class BedrockLibrary
{
	public Map<String, BedrockScheme> presets = new HashMap<String, BedrockScheme>();
	public File folder;

	public BedrockLibrary(File folder)
	{
		this.folder = folder;
		this.folder.mkdirs();
	}

	public void reload()
	{
		this.presets.clear();

		for (File file : this.folder.listFiles())
		{
			if (file.isFile() && file.getName().endsWith(".json"))
			{
				this.loadScheme(file);
			}
		}
	}

	/**
	 * Load a scheme from a file
	 */
	private void loadScheme(File file)
	{
		String name = file.getName();

		try
		{
			BedrockScheme particle = BedrockScheme.parse(FileUtils.readFileToString(file, Charset.defaultCharset()));

			this.presets.put(name.substring(0, name.indexOf(".json")), particle);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}