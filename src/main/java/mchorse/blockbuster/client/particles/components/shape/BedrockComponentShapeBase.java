package mchorse.blockbuster.client.particles.components.shape;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.molang.Molang;
import mchorse.blockbuster.client.particles.molang.MolangExpression;

public abstract class BedrockComponentShapeBase extends BedrockComponentBase
{
	public MolangExpression[] offset = {Molang.ZERO, Molang.ZERO, Molang.ZERO};
	public ShapeDirection direction = ShapeDirection.OUTWARDS;

	@Override
	public BedrockComponentBase fromJson(JsonObject element)
	{
		if (element.has("offset"))
		{
			JsonArray array = element.getAsJsonArray("offset");

			if (array.size() >= 3)
			{
				this.offset[0] = Molang.parse(array.get(0));
				this.offset[1] = Molang.parse(array.get(1));
				this.offset[2] = Molang.parse(array.get(2));
			}
		}

		if (element.has("direction"))
		{
			JsonElement direction = element.get("direction");

			if (direction.isJsonPrimitive())
			{
				String name = direction.getAsString();

				if (name.equals("inwards")) this.direction = ShapeDirection.INWARDS;
				else this.direction = ShapeDirection.OUTWARDS;
			}
			else if (direction.isJsonArray())
			{
				JsonArray array = direction.getAsJsonArray();

				if (array.size() >= 3)
				{
					this.direction = new ShapeDirection.Vector(
						Molang.parse(array.get(0)),
						Molang.parse(array.get(1)),
						Molang.parse(array.get(2))
					);
				}
			}
		}

		return super.fromJson(element);
	}
}