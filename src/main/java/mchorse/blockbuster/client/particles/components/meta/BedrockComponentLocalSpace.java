package mchorse.blockbuster.client.particles.components.meta;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentParticleInitialize;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;

public class BedrockComponentLocalSpace extends BedrockComponentBase implements IComponentParticleInitialize
{
	public boolean position;
	public boolean rotation;

	public BedrockComponentBase fromJson(JsonElement elem)
	{
		if (!elem.isJsonObject()) return super.fromJson(elem);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("position")) this.position = element.get("position").getAsBoolean();
		if (element.has("rotation")) this.rotation = element.get("rotation").getAsBoolean();

		return super.fromJson(element);
	}

	@Override
	public void apply(BedrockEmitter emitter, BedrockParticle particle)
	{
		particle.relative = this.position;
	}
}