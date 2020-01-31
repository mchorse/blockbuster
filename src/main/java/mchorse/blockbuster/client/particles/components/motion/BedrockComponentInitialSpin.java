package mchorse.blockbuster.client.particles.components.motion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentParticleInitialize;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.Molang;
import mchorse.blockbuster.client.particles.molang.MolangExpression;

public class BedrockComponentInitialSpin extends BedrockComponentBase implements IComponentParticleInitialize
{
	public MolangExpression rotation = Molang.ZERO;
	public MolangExpression rate = Molang.ZERO;

	@Override
	public BedrockComponentBase fromJson(JsonElement elem)
	{
		if (!elem.isJsonObject()) return super.fromJson(elem);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("rotation")) this.rotation = Molang.parse(element.get("rotation"));
		if (element.has("rotation_rate")) this.rate = Molang.parse(element.get("rotation_rate"));

		return super.fromJson(element);
	}

	@Override
	public void apply(BedrockParticle particle, BedrockEmitter emitter)
	{
		particle.rotation = this.rotation.evaluate();
		particle.rotationVelocity = this.rate.evaluate();
	}
}