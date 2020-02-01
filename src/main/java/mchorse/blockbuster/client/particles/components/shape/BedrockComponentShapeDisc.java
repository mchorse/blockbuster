package mchorse.blockbuster.client.particles.components.shape;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.Molang;
import mchorse.blockbuster.client.particles.molang.MolangExpression;

import javax.vecmath.Vector3f;

public class BedrockComponentShapeDisc extends BedrockComponentShapeSurfaced
{
	public MolangExpression[] normal = {Molang.ZERO, Molang.ONE, Molang.ZERO};
	public MolangExpression radius = Molang.ONE;

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

		if (element.has("radius")) this.radius = Molang.parse(element.get("radius"));

		return super.fromJson(element);
	}

	@Override
	public void apply(BedrockEmitter emitter, BedrockParticle particle)
	{
		float centerX = this.offset[0].evaluate();
		float centerY = this.offset[1].evaluate();
		float centerZ = this.offset[2].evaluate();

		Vector3f normal = new Vector3f(this.normal[0].evaluate(), this.normal[1].evaluate(), this.normal[2].evaluate());

		normal.normalize();

		/* TODO: normal rotation */
	}
}