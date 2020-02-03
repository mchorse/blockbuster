package mchorse.blockbuster.client.particles.components.rate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentEmitterUpdate;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;

public class BedrockComponentRateInstant extends BedrockComponentBase implements IComponentEmitterUpdate
{
	public int particles;

	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject()) return super.fromJson(elem, parser);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("num_particles")) this.particles = element.get("num_particles").getAsInt();

		return super.fromJson(element, parser);
	}

	@Override
	public void update(BedrockEmitter emitter)
	{
		if (emitter.getAge() == 0)
		{
			emitter.setEmitterVariables(0);

			for (int i = 0; i < this.particles; i ++)
			{
				emitter.spawnParticle();
			}
		}
	}
}