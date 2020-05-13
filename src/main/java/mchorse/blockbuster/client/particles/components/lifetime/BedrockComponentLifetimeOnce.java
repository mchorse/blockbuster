package mchorse.blockbuster.client.particles.components.lifetime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentEmitterUpdate;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;

public class BedrockComponentLifetimeOnce extends BedrockComponentBase implements IComponentEmitterUpdate
{
	public MolangExpression activeTime = BedrockComponentLifetimeLooping.DEFAULT_ACTIVE;

	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject()) return super.fromJson(elem, parser);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("active_time")) this.activeTime = parser.parseJson(element.get("active_time"));

		return super.fromJson(element, parser);
	}

	@Override
	public JsonElement toJson()
	{
		JsonObject object = new JsonObject();

		if (!MolangExpression.isConstant(this.activeTime, 10)) object.add("active_time", this.activeTime.toJson());

		return object;
	}

	@Override
	public void update(BedrockEmitter emitter)
	{
		if (emitter.getAge() >= this.activeTime.get())
		{
			emitter.stop();
		}
	}
}