package mchorse.blockbuster.client.particles.components.shape;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;

public class BedrockComponentShapeSphere extends BedrockComponentShapeSurfaced
{
	public float radius = 1;

	@Override
	public BedrockComponentBase fromJson(JsonElement elem)
	{
		if (!elem.isJsonObject()) return super.fromJson(elem);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("radius")) this.radius = element.get("radius").getAsFloat();

		return super.fromJson(element);
	}
}