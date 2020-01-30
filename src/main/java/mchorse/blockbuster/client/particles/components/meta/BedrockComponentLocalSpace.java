package mchorse.blockbuster.client.particles.components.meta;

import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;

public class BedrockComponentLocalSpace extends BedrockComponentBase
{
	public boolean position;
	public boolean rotation;

	@Override
	public BedrockComponentBase fromJson(JsonObject element)
	{
		if (element.has("position")) this.position = element.get("position").getAsBoolean();
		if (element.has("rotation")) this.rotation = element.get("rotation").getAsBoolean();

		return super.fromJson(element);
	}
}