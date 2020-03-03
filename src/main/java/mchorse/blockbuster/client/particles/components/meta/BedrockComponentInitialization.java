package mchorse.blockbuster.client.particles.components.meta;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentEmitterInitialize;
import mchorse.blockbuster.client.particles.components.IComponentEmitterUpdate;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;

public class BedrockComponentInitialization extends BedrockComponentBase implements IComponentEmitterInitialize, IComponentEmitterUpdate
{
	public MolangExpression creation = MolangParser.ZERO;
	public MolangExpression update = MolangParser.ZERO;

	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject()) return super.fromJson(elem, parser);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("creation_expression")) this.creation = parser.parseJson(element.get("creation_expression"));
		if (element.has("per_update_expression")) this.update = parser.parseJson(element.get("per_update_expression"));

		return super.fromJson(element, parser);
	}

	@Override
	public void apply(BedrockEmitter emitter)
	{
		this.creation.get();
	}

	@Override
	public void update(BedrockEmitter emitter)
	{
		this.update.get();
	}
}