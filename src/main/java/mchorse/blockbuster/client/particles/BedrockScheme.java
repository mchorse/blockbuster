package mchorse.blockbuster.client.particles;

import com.google.gson.GsonBuilder;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentEmitterInitialize;
import mchorse.blockbuster.client.particles.components.IComponentEmitterUpdate;
import mchorse.blockbuster.client.particles.components.IComponentParticleInitialize;
import mchorse.blockbuster.client.particles.components.IComponentParticleRender;
import mchorse.blockbuster.client.particles.components.IComponentParticleUpdate;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BedrockScheme
{
	public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(Blockbuster.MOD_ID, "textures/default_particles.png");

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

	/* MoLang integration */
	public MolangParser parser = new MolangParser();

	public static BedrockScheme parse(String json)
	{
		return new GsonBuilder()
			.registerTypeAdapter(BedrockScheme.class, new BedrockSchemeJsonAdapter())
			.create()
			.fromJson(json, BedrockScheme.class);
	}

	public void setup()
	{
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

	public  <T extends IComponentBase> List<T> getComponents(Class<T> clazz)
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