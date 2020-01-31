package mchorse.blockbuster.client.particles.components.shape;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.molang.Molang;
import mchorse.blockbuster.client.particles.molang.MolangExpression;

public class BedrockComponentShapeDisc extends BedrockComponentShapeSurfaced
{
	public MolangExpression[] normal = {Molang.ZERO, Molang.ONE, Molang.ZERO};
	public float radius = 1;

	@Override
	public BedrockComponentBase fromJson(JsonElement elem)
	{
		if (!elem.isJsonObject()) return super.fromJson(elem);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("plane_normal"))
		{
			JsonArray array = element.getAsJsonArray("plane_normal");

			if (array.size() >= 3)
			{
				this.normal[0] = Molang.parse(array.get(0));
				this.normal[1] = Molang.parse(array.get(1));
				this.normal[2] = Molang.parse(array.get(2));
			}
		}

		if (element.has("radius")) this.radius = element.get("radius").getAsFloat();

		return super.fromJson(element);
	}
}