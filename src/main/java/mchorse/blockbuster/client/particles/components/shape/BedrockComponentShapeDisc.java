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

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class BedrockComponentShapeDisc extends BedrockComponentShapeSurfaced
{
	public MolangExpression[] normal = {MolangParser.ZERO, MolangParser.ONE, MolangParser.ZERO};
	public MolangExpression radius = MolangParser.ONE;

	@Override
	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject()) return super.fromJson(elem, parser);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("plane_normal"))
		{
			JsonArray array = element.getAsJsonArray("plane_normal");

			if (array.size() >= 3)
			{
				this.normal[0] = parser.parseJson(array.get(0));
				this.normal[1] = parser.parseJson(array.get(1));
				this.normal[2] = parser.parseJson(array.get(2));
			}
		}

		if (element.has("radius")) this.radius = parser.parseJson(element.get("radius"));

		return super.fromJson(element, parser);
	}

	@Override
	public void apply(BedrockEmitter emitter, BedrockParticle particle)
	{
		float centerX = (float) this.offset[0].get();
		float centerY = (float) this.offset[1].get();
		float centerZ = (float) this.offset[2].get();

		Vector3f normal = new Vector3f((float) this.normal[0].get(), (float) this.normal[1].get(), (float) this.normal[2].get());

		normal.normalize();

		/* TODO: implement normal */

		Vector3d position = new Vector3d(Math.random() - 0.5, 0, Math.random() - 0.5);
		position.normalize();
		position.scale(this.radius.get() * (this.surface ? 1 : Math.random()));

		particle.prevX = particle.x = position.x + centerX;
		particle.prevY = particle.y = position.y + centerY;
		particle.prevZ = particle.z = position.z + centerZ;

		this.direction.applyDirection(particle, centerX, centerY, centerZ);
	}
}