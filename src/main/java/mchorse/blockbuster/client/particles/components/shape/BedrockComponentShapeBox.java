package mchorse.blockbuster.client.particles.components.shape;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.molang.Molang;
import mchorse.blockbuster.client.particles.molang.MolangExpression;

public class BedrockComponentShapeBox extends BedrockComponentShapeSurfaced
{
	public MolangExpression[] dimensions = {Molang.ZERO, Molang.ZERO, Molang.ZERO};

	public BedrockComponentBase fromJson(JsonElement elem)
	{
		if (!elem.isJsonObject()) return super.fromJson(elem);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("half_dimensions"))
		{
			JsonArray array = element.getAsJsonArray("half_dimensions");

			if (array.size() >= 3)
			{
				this.dimensions[0] = Molang.parse(array.get(0));
				this.dimensions[1] = Molang.parse(array.get(1));
				this.dimensions[2] = Molang.parse(array.get(2));
			}
		}

		return super.fromJson(element);
	}
}