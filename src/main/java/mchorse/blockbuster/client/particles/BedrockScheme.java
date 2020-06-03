package mchorse.blockbuster.client.particles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentEmitterInitialize;
import mchorse.blockbuster.client.particles.components.IComponentEmitterUpdate;
import mchorse.blockbuster.client.particles.components.IComponentParticleInitialize;
import mchorse.blockbuster.client.particles.components.IComponentParticleRender;
import mchorse.blockbuster.client.particles.components.IComponentParticleUpdate;
import mchorse.blockbuster.client.particles.components.motion.BedrockComponentInitialSpeed;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BedrockScheme
{
	public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(Blockbuster.MOD_ID, "textures/default_particles.png");
	public static final Gson JSON_PARSER = new GsonBuilder()
		.registerTypeAdapter(BedrockScheme.class, new BedrockSchemeJsonAdapter())
		.create();

	/* Particles identifier */
	public String identifier = "";

	/* Particle description */
	public BedrockMaterial material = BedrockMaterial.OPAQUE;
	public ResourceLocation texture = DEFAULT_TEXTURE;

	/* Particle's curves */
	public Map<String, BedrockCurve> curves = new HashMap<String, BedrockCurve>();

	/* Particle's components */
	public List<BedrockComponentBase> components = new ArrayList<BedrockComponentBase>();
	public List<IComponentEmitterInitialize> emitterInitializes;
	public List<IComponentEmitterUpdate> emitterUpdates;
	public List<IComponentParticleInitialize> particleInitializes;
	public List<IComponentParticleUpdate> particleUpdates;
	public List<IComponentParticleRender> particleRender;

	private boolean factory;

	/* MoLang integration */
	public MolangParser parser = new MolangParser();

	public static BedrockScheme parse(String json)
	{
		return JSON_PARSER.fromJson(json, BedrockScheme.class);
	}

	public static BedrockScheme parse(JsonElement json)
	{
		return JSON_PARSER.fromJson(json, BedrockScheme.class);
	}

	public static JsonElement toJson(BedrockScheme scheme)
	{
		return JSON_PARSER.toJsonTree(scheme);
	}

	/**
	 * Probably it's very expensive, but it's much easier than implementing copy methods
	 * to every component in the particle system...
	 */
	public static BedrockScheme dupe(BedrockScheme scheme)
	{
		return parse(toJson(scheme));
	}

	public BedrockScheme factory(boolean factory)
	{
		this.factory = factory;

		return this;
	}

	public boolean isFactory()
	{
		return this.factory;
	}

	public void setup()
	{
		this.getOrCreate(BedrockComponentInitialSpeed.class);

		this.emitterInitializes = this.getComponents(IComponentEmitterInitialize.class);
		this.emitterUpdates = this.getComponents(IComponentEmitterUpdate.class);
		this.particleInitializes = this.getComponents(IComponentParticleInitialize.class);
		this.particleUpdates = this.getComponents(IComponentParticleUpdate.class);
		this.particleRender = this.getComponents(IComponentParticleRender.class);

		/* Link variables with curves */
		for (Map.Entry<String, BedrockCurve> entry : this.curves.entrySet())
		{
			entry.getValue().variable = this.parser.variables.get(entry.getKey());
		}
	}

	public <T extends IComponentBase> List<T> getComponents(Class<T> clazz)
	{
		List<T> list = new ArrayList<T>();

		for (BedrockComponentBase component : this.components)
		{
			if (clazz.isAssignableFrom(component.getClass()))
			{
				list.add((T) component);
			}
		}

		if (list.size() > 1)
		{
			Collections.sort(list, Comparator.comparingInt(IComponentBase::getSortingIndex));
		}

		return list;
	}

	public <T extends BedrockComponentBase> T get(Class<T> clazz)
	{
		for (BedrockComponentBase component : this.components)
		{
			if (clazz.isAssignableFrom(component.getClass()))
			{
				return (T) component;
			}
		}

		return null;
	}

	public <T extends BedrockComponentBase> T add(Class<T> clazz)
	{
		T result = null;

		try
		{
			result = (T) clazz.getConstructor().newInstance();

			this.components.add(result);
			this.setup();
		}
		catch (Exception e)
		{}

		return result;
	}

	public <T extends BedrockComponentBase> T getOrCreate(Class<T> clazz)
	{
		return this.getOrCreate(clazz, clazz);
	}

	public <T extends BedrockComponentBase> T getOrCreate(Class<T> clazz, Class subclass)
	{
		T result = this.get(clazz);

		if (result == null)
		{
			result = (T) this.add(subclass);
		}

		return result;
	}

	public <T extends BedrockComponentBase> T remove(Class<T> clazz)
	{
		Iterator<BedrockComponentBase> it = this.components.iterator();

		while (it.hasNext())
		{
			BedrockComponentBase component = it.next();

			if (clazz.isAssignableFrom(component.getClass()))
			{
				it.remove();

				return (T) component;
			}
		}

		return null;
	}

	public <T extends BedrockComponentBase> T replace(Class<T> clazz, Class subclass)
	{
		this.remove(clazz);

		return (T) this.add(subclass);
	}

	/**
	 * Update curve values
	 */
	public void updateCurves()
	{
		for (BedrockCurve curve : this.curves.values())
		{
			if (curve.variable != null)
			{
				curve.variable.set(curve.compute());
			}
		}
	}
}