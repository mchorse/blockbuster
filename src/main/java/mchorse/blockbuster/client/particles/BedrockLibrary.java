package mchorse.blockbuster.client.particles;

import com.google.gson.JsonElement;
import mchorse.mclib.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class BedrockLibrary
{
	public Map<String, BedrockScheme> presets = new HashMap<String, BedrockScheme>();
	public Map<String, BedrockScheme> factory = new HashMap<String, BedrockScheme>();
	public File folder;

	public BedrockLibrary(File folder)
	{
		this.folder = folder;
		this.folder.mkdirs();

		/* Load factory (default) presets */
		this.loadFactory("default_fire", this.getClass().getClassLoader().getResourceAsStream("assets/blockbuster/particles/fire.json"));
		this.loadFactory("default_magic", this.getClass().getClassLoader().getResourceAsStream("assets/blockbuster/particles/magic.json"));
		this.loadFactory("default_rain", this.getClass().getClassLoader().getResourceAsStream("assets/blockbuster/particles/rain.json"));
		this.loadFactory("default_snow", this.getClass().getClassLoader().getResourceAsStream("assets/blockbuster/particles/snow.json"));
	}

	public void reload()
	{
		this.presets.clear();
		this.presets.putAll(this.factory);

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

	/**
	 * Load a scheme from a file
	 */
	private void loadFactory(String name, InputStream stream)
	{
		try
		{
			BedrockScheme particle = BedrockScheme.parse(IOUtils.toString(stream, Charset.defaultCharset()));

			this.factory.put(name, particle);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void save(String filename, BedrockScheme scheme)
	{
		String json = JsonUtils.jsonToPretty(BedrockScheme.toJson(scheme));
		File file = new File(this.folder, filename + ".json");

		try
		{
			FileUtils.writeStringToFile(file, json, Charset.defaultCharset());
		}
		catch (Exception e)
		{}

		this.loadScheme(file);
	}
}