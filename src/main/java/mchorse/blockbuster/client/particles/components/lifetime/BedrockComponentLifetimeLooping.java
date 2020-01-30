package mchorse.blockbuster.client.particles.components.lifetime;

import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.molang.Molang;
import mchorse.blockbuster.client.particles.molang.MolangExpression;

public class BedrockComponentLifetimeLooping extends BedrockComponentBase
{
	public MolangExpression activeTime;
	public MolangExpression sleepTime;

	@Override
	public BedrockComponentBase fromJson(JsonObject element)
	{
		if (element.has("active_time")) this.activeTime = Molang.parse(element.get("active_time"));
		if (element.has("sleep_time")) this.sleepTime = Molang.parse(element.get("sleep_time"));

		return super.fromJson(element);
	}
}