package mchorse.blockbuster.client.particles.components.shape;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;

public class BedrockComponentShapeBox extends BedrockComponentShapeBase
{
	public MolangExpression[] halfDimensions = {MolangParser.ZERO, MolangParser.ZERO, MolangParser.ZERO};

	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject()) return super.fromJson(elem, parser);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("half_dimensions"))
		{
			JsonArray array = element.getAsJsonArray("half_dimensions");

			if (array.size() >= 3)
			{
				this.halfDimensions[0] = parser.parseJson(array.get(0));
				this.halfDimensions[1] = parser.parseJson(array.get(1));
				this.halfDimensions[2] = parser.parseJson(array.get(2));
			}
		}

		return super.fromJson(element, parser);
	}

	@Override
	public JsonElement toJson()
	{
		JsonObject object = (JsonObject) super.toJson();
		JsonArray array = new JsonArray();

		for (MolangExpression expression : this.halfDimensions)
		{
			array.add(expression.toJson());
		}

		object.add("half_dimensions", array);

		return object;
	}

	@Override
	public void apply(BedrockEmitter emitter, BedrockParticle particle)
	{
		float centerX = (float) this.offset[0].get();
		float centerY = (float) this.offset[1].get();
		float centerZ = (float) this.offset[2].get();

		float w = (float) this.halfDimensions[0].get();
		float h = (float) this.halfDimensions[1].get();
		float d = (float) this.halfDimensions[2].get();

		particle.position.x = centerX + ((float) Math.random() * 2 - 1F) * w;
		particle.position.y = centerY + ((float) Math.random() * 2 - 1F) * h;
		particle.position.z = centerZ + ((float) Math.random() * 2 - 1F) * d;

		if (this.surface)
		{
			int roll = (int) (Math.random() * 6 * 100) % 6;

			if (roll == 0) particle.position.x = centerX + w;
			else if (roll == 1) particle.position.x = centerX - w;
			else if (roll == 2) particle.position.y = centerY + h;
			else if (roll == 3) particle.position.y = centerY - h;
			else if (roll == 4) particle.position.z = centerZ + d;
			else if (roll == 5) particle.position.z = centerZ - d;
		}

		this.direction.applyDirection(particle, centerX, centerY, centerZ);
	}
}