package mchorse.blockbuster.client.particles.components.lifetime;

import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.molang.Molang;
import mchorse.blockbuster.client.particles.molang.MolangExpression;

public class BedrockComponentLifetimeExpression extends BedrockComponentBase
{
	public MolangExpression activation;
	public MolangExpression expiration;

	@Override
	public BedrockComponentBase fromJson(JsonObject element)
	{
		if (element.has("activation_expression")) this.activation = Molang.parse(element.get("activation_expression"));
		if (element.has("expiration_expression")) this.expiration = Molang.parse(element.get("expiration_expression"));

		return super.fromJson(element);
	}
}