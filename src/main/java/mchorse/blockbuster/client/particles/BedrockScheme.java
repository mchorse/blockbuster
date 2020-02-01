package mchorse.blockbuster.client.particles;

import com.google.gson.GsonBuilder;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BedrockScheme
{
	/* Particles identifier */
	public String identifier = "";

	/* Particle description */
	public BedrockMaterial material = BedrockMaterial.OPAQUE;
	public ResourceLocation texture = new ResourceLocation(Blockbuster.MODID, "textures/default_particles.png");

	/* Particle's curves */
	public Map<String, BedrockCurve> curves = new HashMap<String, BedrockCurve>();

	/* Particle's components */
	public List<BedrockComponentBase> components = new ArrayList<BedrockComponentBase>();

	public static BedrockScheme parse(String json)
	{
		return new GsonBuilder()
			.registerTypeAdapter(BedrockScheme.class, new BedrockSchemeJsonAdapter())
			.create()
			.fromJson(json, BedrockScheme.class);
	}

	public <T> T getComponent(Class<T> clazz)
	{
		List<T> components = this.getComponents(clazz);

		return components.isEmpty() ? null : components.get(0);
	}

	public <T> List<T> getComponents(Class<T> clazz)
	{
		List<T> list = new ArrayList<T>();

		for (BedrockComponentBase component : this.components)
		{
			if (clazz.isAssignableFrom(component.getClass()))
			{
				list.add((T) component);
			}
		}

		return list;
	}
}