package mchorse.blockbuster.client.particles;

import mchorse.mclib.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class BedrockLibrary
{
	public static long lastUpdate;

	public Map<String, BedrockScheme> presets = new HashMap<String, BedrockScheme>();
	public Map<String, BedrockScheme> factory = new HashMap<String, BedrockScheme>();
	public File folder;

	public BedrockLibrary(File folder)
	{
		this.folder = folder;
		this.folder.mkdirs();

		/* Load factory (default) presets */
		this.storeFactory("default_fire");
		this.storeFactory("default_magic");
		this.storeFactory("default_rain");
		this.storeFactory("default_snow");
	}

	public File file(String name)
	{
		return new File(this.folder, name + ".json");
	}

	public boolean hasEffect(String name)
	{
		return this.file(name).isFile();
	}

	public void reload()
	{
		this.presets.clear();
		this.presets.putAll(this.factory);

		for (File file : this.folder.listFiles())
		{
			if (file.isFile() && file.getName().endsWith(".json"))
			{
				this.storeScheme(file);
			}
		}
	}

	public BedrockScheme load(String name)
	{
		BedrockScheme scheme = this.loadScheme(this.file(name));

		if (scheme != null)
		{
			return scheme;
		}

		return this.loadFactory(name);
	}

	private void storeScheme(File file)
	{
		BedrockScheme scheme = this.loadScheme(file);

		if (scheme != null)
		{
			String name = file.getName();

			this.presets.put(name.substring(0, name.indexOf(".json")), scheme);
		}
	}

	/**
	 * Load a scheme from a file
	 */
	public BedrockScheme loadScheme(File file)
	{
		if (!file.exists())
		{
			return null;
		}

		try
		{
			return BedrockScheme.parse(FileUtils.readFileToString(file, Charset.defaultCharset()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	private void storeFactory(String name)
	{
		BedrockScheme scheme = this.loadFactory(name);

		if (scheme != null)
		{
			this.factory.put(name, scheme);
		}
	}

	/**
	 * Load a scheme from Blockbuster's zip
	 */
	public BedrockScheme loadFactory(String name)
	{
		try
		{
			return BedrockScheme.parse(IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("assets/blockbuster/particles/" + name + ".json"), Charset.defaultCharset())).factory(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public void save(String filename, BedrockScheme scheme)
	{
		String json = JsonUtils.jsonToPretty(BedrockScheme.toJson(scheme));
		File file = this.file(filename);

		try
		{
			FileUtils.writeStringToFile(file, json, Charset.defaultCharset());
		}
		catch (Exception e)
		{}

		this.storeScheme(file);

		lastUpdate = System.currentTimeMillis();
	}
}