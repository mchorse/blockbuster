package mchorse.blockbuster.client.particles.components.shape;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.Molang;
import mchorse.blockbuster.client.particles.molang.MolangExpression;

import javax.vecmath.Vector3f;

public class BedrockComponentShapeSphere extends BedrockComponentShapeSurfaced
{
	public MolangExpression radius = Molang.ONE;

	@Override
	public BedrockComponentBase fromJson(JsonElement elem)
	{
		if (!elem.isJsonObject()) return super.fromJson(elem);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("radius")) this.radius = Molang.parse(element.get("radius"));

		return super.fromJson(element);
	}

	@Override
	public void apply(BedrockEmitter emitter, BedrockParticle particle)
	{
		float centerX = this.offset[0].evaluate();
		float centerY = this.offset[1].evaluate();
		float centerZ = this.offset[2].evaluate();
		float radius = this.radius.evaluate();

		Vector3f direction = new Vector3f((float) Math.random() * 2 - 1, (float) Math.random() * 2 - 1, (float) Math.random() * 2 - 1);
		direction.normalize();

		if (!this.surface)
		{
			radius *= Math.random();
		}

		direction.scale(radius);

		particle.x = particle.prevX = centerX + direction.x;
		particle.y = particle.prevY = centerY + direction.y;
		particle.z = particle.prevZ = centerZ + direction.z;

		this.direction.applyDirection(particle, centerX, centerY, centerZ);
	}
}