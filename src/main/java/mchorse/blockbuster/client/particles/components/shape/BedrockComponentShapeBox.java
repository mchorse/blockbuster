package mchorse.blockbuster.client.particles.components.shape;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.Molang;
import mchorse.blockbuster.client.particles.molang.MolangExpression;

public class BedrockComponentShapeBox extends BedrockComponentShapeSurfaced
{
	public MolangExpression[] halfDimensions = {Molang.ZERO, Molang.ZERO, Molang.ZERO};

	public BedrockComponentBase fromJson(JsonElement elem)
	{
		if (!elem.isJsonObject()) return super.fromJson(elem);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("half_dimensions"))
		{
			JsonArray array = element.getAsJsonArray("half_dimensions");

			if (array.size() >= 3)
			{
				this.halfDimensions[0] = Molang.parse(array.get(0));
				this.halfDimensions[1] = Molang.parse(array.get(1));
				this.halfDimensions[2] = Molang.parse(array.get(2));
			}
		}

		return super.fromJson(element);
	}

	@Override
	public void apply(BedrockParticle particle, BedrockEmitter emitter)
	{
		float centerX = this.offset[0].evaluate();
		float centerY = this.offset[1].evaluate();
		float centerZ = this.offset[2].evaluate();

		float w = this.halfDimensions[0].evaluate();
		float h = this.halfDimensions[1].evaluate();
		float d = this.halfDimensions[2].evaluate();

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