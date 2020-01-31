package mchorse.blockbuster.client.particles.components.expiration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.molang.Molang;
import mchorse.blockbuster.client.particles.molang.MolangExpression;

public class BedrockComponentLifetime extends BedrockComponentBase
{
	public MolangExpression expression;
	public boolean max;

	@Override
	public BedrockComponentBase fromJson(JsonElement elem)
	{
		if (!elem.isJsonObject()) return super.fromJson(elem);

		JsonObject element = elem.getAsJsonObject();
		JsonElement expression = null;

		if (element.has("expiration_expression"))
		{
			expression = element.get("expiration_expression");
			this.max = false;
		}
		else if (element.has("max_lifetime"))
		{
			expression = element.get("max_lifetime");
			this.max = true;
		}
		else
		{
			throw new JsonParseException("No expiration_expression or max_lifetime was found in minecraft:particle_lifetime_expression component");
		}

		this.expression = Molang.parse(expression);

		return super.fromJson(element);
	}
}