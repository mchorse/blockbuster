package mchorse.blockbuster.client.particles.components.motion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentParticleUpdate;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;

public class BedrockComponentMotionParametric extends BedrockComponentBase implements IComponentParticleUpdate
{
	public MolangExpression[] position = {MolangParser.ZERO, MolangParser.ZERO, MolangParser.ZERO};
	public MolangExpression rotation = MolangParser.ZERO;

	@Override
	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject()) return super.fromJson(elem, parser);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("relative_position") && element.get("relative_position").isJsonArray())
		{
			JsonArray array = element.get("relative_position").getAsJsonArray();

			this.position[0] = parser.parseJson(array.get(0));
			this.position[1] = parser.parseJson(array.get(1));
			this.position[2] = parser.parseJson(array.get(2));
		}

		if (element.has("rotation"))
		{
			this.rotation = parser.parseJson(element.get("rotation"));
		}

		return super.fromJson(element, parser);
	}

	@Override
	public void update(BedrockEmitter emitter, BedrockParticle particle)
	{
		particle.manual = true;
		particle.position.x += this.position[0].get();
		particle.position.y += this.position[1].get();
		particle.position.z += this.position[2].get();
		particle.rotation = (float) this.rotation.get();
	}
}