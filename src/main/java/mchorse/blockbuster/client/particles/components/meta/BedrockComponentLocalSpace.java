package mchorse.blockbuster.client.particles.components.meta;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;

public class BedrockComponentLocalSpace extends BedrockComponentBase
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
}