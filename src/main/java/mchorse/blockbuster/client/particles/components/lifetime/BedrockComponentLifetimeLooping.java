package mchorse.blockbuster.client.particles.components.lifetime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentEmitterUpdate;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;
import mchorse.blockbuster.client.particles.molang.expressions.MolangValue;
import mchorse.mclib.math.Constant;

public class BedrockComponentLifetimeLooping extends BedrockComponentBase implements IComponentEmitterUpdate
{
	public MolangExpression activeTime = new MolangValue(null, new Constant(10));
	public MolangExpression sleepTime = MolangParser.ZERO;

	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject()) return super.fromJson(elem, parser);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("active_time")) this.activeTime = parser.parseJson(element.get("active_time"));
		if (element.has("sleep_time")) this.sleepTime = parser.parseJson(element.get("sleep_time"));

		return super.fromJson(element, parser);
	}

	@Override
	public void update(BedrockEmitter emitter)
	{
		double active = this.activeTime.get();
		double sleep = this.sleepTime.get();
		double age = emitter.getAge();

		if (age >= active)
		{
			emitter.stop();
		}

		if (age >= active + sleep)
		{
			emitter.start();
		}
	}
}