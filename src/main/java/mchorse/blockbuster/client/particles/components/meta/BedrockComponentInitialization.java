package mchorse.blockbuster.client.particles.components.meta;

import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.molang.Molang;
import mchorse.blockbuster.client.particles.molang.MolangExpression;

public class BedrockComponentInitialization extends BedrockComponentBase
{
	public MolangExpression creation;
	public MolangExpression update;

	@Override
	public BedrockComponentBase fromJson(JsonObject element)
	{
		if (element.has("creation_expression")) this.creation = Molang.parse(element.get("creation_expression"));
		if (element.has("per_update_expression")) this.update = Molang.parse(element.get("per_update_expression"));

		return super.fromJson(element);
	}
}