package mchorse.blockbuster.client.particles.components.motion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentParticleInitialize;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.Molang;
import mchorse.blockbuster.client.particles.molang.MolangExpression;

public class BedrockComponentInitialSpeed extends BedrockComponentBase implements IComponentParticleInitialize
{
	public MolangExpression speed = Molang.ZERO;
	public MolangExpression[] direction;

	@Override
	public BedrockComponentBase fromJson(JsonElement element)
	{
		if (element.isJsonArray())
		{
			JsonArray array = element.getAsJsonArray();

			if (array.size() >= 3)
			{
				this.direction = new MolangExpression[] {Molang.parse(array.get(0)), Molang.parse(array.get(1)), Molang.parse(array.get(2))};
			}
		}
		else if (element.isJsonPrimitive())
		{
			this.speed = Molang.parse(element);
		}

		return super.fromJson(element);
	}

	@Override
	public void apply(BedrockEmitter emitter, BedrockParticle particle)
	{
		if (this.direction != null)
		{
			particle.motionX = this.direction[0].evaluate() / 20F;
			particle.motionY = this.direction[1].evaluate() / 20F;
			particle.motionZ = this.direction[2].evaluate() / 20F;
		}
		else
		{
			float speed = this.speed.evaluate() / 20F;

			particle.motionX *= speed;
			particle.motionY *= speed;
			particle.motionZ *= speed;
		}
	}
}