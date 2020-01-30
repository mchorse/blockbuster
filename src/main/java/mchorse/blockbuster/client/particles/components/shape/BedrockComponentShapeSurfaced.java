package mchorse.blockbuster.client.particles.components.shape;

import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;

public abstract class BedrockComponentShapeSurfaced extends BedrockComponentShapeBase
{
	public boolean surface = false;

	@Override
	public BedrockComponentBase fromJson(JsonObject element)
	{
		if (element.has("surface_only")) this.surface = element.get("surface_only").getAsBoolean();

		return super.fromJson(element);
	}
}