package mchorse.blockbuster.client.particles.components.rate;

import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;

public class BedrockComponentRateInstant extends BedrockComponentBase
{
	public int particles;

	@Override
	public BedrockComponentBase fromJson(JsonObject element)
	{
		if (element.has("num_particles")) this.particles = element.get("num_particles").getAsInt();

		return super.fromJson(element);
	}
}