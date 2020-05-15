package mchorse.blockbuster.client.particles.components.lifetime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentEmitterUpdate;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;
import mchorse.mclib.math.Operation;

public class BedrockComponentLifetimeExpression extends BedrockComponentLifetime
{
	public MolangExpression expiration = MolangParser.ZERO;

	@Override
	protected String getPropertyName()
	{
		return "activation_expression";
	}

	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject())
		{
			return super.fromJson(elem, parser);
		}

		JsonObject element = elem.getAsJsonObject();

		if (element.has("expiration_expression"))
		{
			this.expiration = parser.parseJson(element.get("expiration_expression"));
		}

		return super.fromJson(element, parser);
	}

	@Override
	public JsonElement toJson()
	{
		JsonObject object = (JsonObject) super.toJson();

		if (!MolangExpression.isZero(this.expiration))
		{
			object.add("expiration_expression", this.expiration.toJson());
		}

		return object;
	}

	@Override
	public void update(BedrockEmitter emitter)
	{
		if (!Operation.equals(this.activeTime.get(), 0))
		{
			emitter.start();
		}

		if (!Operation.equals(this.expiration.get(), 0))
		{
			emitter.stop();
		}
	}
}