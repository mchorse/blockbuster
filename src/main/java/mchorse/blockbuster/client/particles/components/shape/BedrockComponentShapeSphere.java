package mchorse.blockbuster.client.particles.components.shape;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;

import javax.vecmath.Vector3f;

public class BedrockComponentShapeSphere extends BedrockComponentShapeSurfaced
{
	public MolangExpression radius = MolangParser.ONE;

	@Override
	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject()) return super.fromJson(elem, parser);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("radius")) this.radius = parser.parseJson(element.get("radius"));

		return super.fromJson(element, parser);
	}

	@Override
	public void apply(BedrockEmitter emitter, BedrockParticle particle)
	{
		float centerX = (float) this.offset[0].get();
		float centerY = (float) this.offset[1].get();
		float centerZ = (float) this.offset[2].get();
		float radius = (float) this.radius.get();

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