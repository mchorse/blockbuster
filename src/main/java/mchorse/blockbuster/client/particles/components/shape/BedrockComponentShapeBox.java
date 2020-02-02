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

public class BedrockComponentShapeBox extends BedrockComponentShapeSurfaced
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
	public void apply(BedrockEmitter emitter, BedrockParticle particle)
	{
		float centerX = (float) this.offset[0].get();
		float centerY = (float) this.offset[1].get();
		float centerZ = (float) this.offset[2].get();

		float w = (float) this.halfDimensions[0].get();
		float h = (float) this.halfDimensions[1].get();
		float d = (float) this.halfDimensions[2].get();

		particle.x = particle.prevX = centerX + ((float) Math.random() - 0.5F) * w;
		particle.y = particle.prevY = centerY + ((float) Math.random() - 0.5F) * h;
		particle.z = particle.prevZ = centerZ + ((float) Math.random() - 0.5F) * d;

		if (this.surface)
		{
			int roll = (int) (Math.random() * 6 * 100) % 6;

			if (roll == 0) particle.x += w / 2F;
			else if (roll == 1) particle.x -= w / 2F;
			else if (roll == 2) particle.y += h / 2F;
			else if (roll == 3) particle.y -= h / 2F;
			else if (roll == 4) particle.z += d / 2F;
			else if (roll == 5) particle.z -= d / 2F;
		}

		this.direction.applyDirection(particle, centerX, centerY, centerZ);
	}
}