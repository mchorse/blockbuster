package mchorse.blockbuster.client.particles.components.rate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentParticleRender;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;
import mchorse.blockbuster.client.particles.molang.expressions.MolangValue;
import mchorse.mclib.math.Constant;
import net.minecraft.client.renderer.BufferBuilder;

public class BedrockComponentRateSteady extends BedrockComponentRate implements IComponentParticleRender
{
	public static final MolangExpression DEFAULT_PARTICLES = new MolangValue(null, new Constant(50));

	public MolangExpression spawnRate = MolangParser.ONE;

	public BedrockComponentRateSteady()
	{
		this.particles = DEFAULT_PARTICLES;
	}

	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject()) return super.fromJson(elem, parser);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("spawn_rate")) this.spawnRate = parser.parseJson(element.get("spawn_rate"));
		if (element.has("max_particles")) this.particles = parser.parseJson(element.get("max_particles"));

		return super.fromJson(element, parser);
	}

	@Override
	public JsonElement toJson()
	{
		JsonObject object = new JsonObject();

		if (!MolangExpression.isOne(this.spawnRate)) object.add("spawn_rate", this.spawnRate.toJson());
		if (!MolangExpression.isConstant(this.particles, 50)) object.add("max_particles", this.particles.toJson());

		return object;
	}

	@Override
	public void preRender(BedrockEmitter emitter, float partialTicks)
	{}

	@Override
	public void render(BedrockEmitter emitter, BedrockParticle particle, BufferBuilder builder, float partialTicks)
	{}

	@Override
	public void renderOnScreen(BedrockParticle particle, int x, int y, float scale, float partialTicks)
	{}

	@Override
	public void postRender(BedrockEmitter emitter, float partialTicks)
	{
		if (emitter.playing)
		{
			double particles = emitter.getAge(partialTicks) * this.spawnRate.get();
			double diff = particles - emitter.spawnedParticles;
			double spawn = Math.ceil(diff);

			if (spawn > 0)
			{
				emitter.setEmitterVariables(partialTicks);

				for (int i = 0; i < spawn; i++)
				{
					if (emitter.particles.size() < this.particles.get())
					{
						emitter.spawnParticle();
					}
				}

				emitter.spawnedParticles += spawn;
			}
		}
	}

	@Override
	public int getSortingIndex()
	{
		return 10;
	}
}