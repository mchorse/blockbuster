package mchorse.blockbuster.client.particles.components.lifetime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;

public class BedrockComponentLifetimeLooping extends BedrockComponentBase
{
	public MolangExpression activeTime;
	public MolangExpression sleepTime;

	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject()) return super.fromJson(elem, parser);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("active_time")) this.activeTime = parser.parseJson(element.get("active_time"));
		if (element.has("sleep_time")) this.sleepTime = parser.parseJson(element.get("sleep_time"));

		return super.fromJson(element, parser);
	}
}