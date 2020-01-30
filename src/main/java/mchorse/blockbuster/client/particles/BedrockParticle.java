package mchorse.blockbuster.client.particles;

import com.google.gson.GsonBuilder;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BedrockParticle
{
	/* Particles identifier */
	public String identifier = "";

	/* Particle description */
	public BedrockMaterial material = BedrockMaterial.OPAQUE;
	public ResourceLocation texture = new ResourceLocation(Blockbuster.MODID, "textures/");

	/* Particle's curves */
	public Map<String, BedrockCurve> curves = new HashMap<String, BedrockCurve>();

	/* Particle's components */
	public List<BedrockComponentBase> components = new ArrayList<BedrockComponentBase>();

	public static BedrockParticle parse(String json)
	{
		return new GsonBuilder()
			.registerTypeAdapter(BedrockParticle.class, new BedrockParticleJsonAdapter())
			.create()
			.fromJson(json, BedrockParticle.class);
	}
}