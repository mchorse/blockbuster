package mchorse.blockbuster.client.particles.components.motion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentParticleInitialize;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;

public class BedrockComponentInitialSpeed extends BedrockComponentBase implements IComponentParticleInitialize
{
	public MolangExpression speed = MolangParser.ONE;
	public MolangExpression[] direction;

	@Override
	public BedrockComponentBase fromJson(JsonElement element, MolangParser parser) throws MolangException
	{
		if (element.isJsonArray())
		{
			JsonArray array = element.getAsJsonArray();

			if (array.size() >= 3)
			{
				this.direction = new MolangExpression[] {parser.parseJson(array.get(0)), parser.parseJson(array.get(1)), parser.parseJson(array.get(2))};
			}
		}
		else if (element.isJsonPrimitive())
		{
			this.speed = parser.parseJson(element);
		}

		return super.fromJson(element, parser);
	}

	@Override
	public JsonElement toJson()
	{
		if (this.direction != null)
		{
			JsonArray array = new JsonArray();

			for (MolangExpression expression : this.direction)
			{
				array.add(expression.toJson());
			}

			return array;
		}

		return this.speed.toJson();
	}

	@Override
	public boolean canBeEmpty()
	{
		return true;
	}

	@Override
	public void apply(BedrockEmitter emitter, BedrockParticle particle)
	{
		if (this.direction != null)
		{
			particle.speed.set(
				(float) this.direction[0].get(),
				(float) this.direction[1].get(),
				(float) this.direction[1].get()
			);
		}
		else
		{
			float speed = (float) this.speed.get();

			particle.speed.scale(speed);
		}
	}

	@Override
	public int getSortingIndex()
	{
		return 5;
	}
}