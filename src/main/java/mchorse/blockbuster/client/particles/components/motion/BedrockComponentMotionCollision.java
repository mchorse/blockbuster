package mchorse.blockbuster.client.particles.components.motion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;

public class BedrockComponentMotionCollision extends BedrockComponentBase
{
	public MolangExpression enabled = MolangParser.ONE;
	public float collissionDrag;
	public float bounciness;
	public float radius;
	public boolean expireOnImpact;

	@Override
	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject()) return super.fromJson(elem, parser);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("enabled")) this.enabled = parser.parseJson(element.get("enabled"));
		if (element.has("collision_drag")) this.collissionDrag = element.get("collision_drag").getAsFloat();
		if (element.has("coefficient_of_restitution")) this.bounciness = element.get("coefficient_of_restitution").getAsFloat();
		if (element.has("collision_radius")) this.radius = element.get("collision_radius").getAsFloat();
		if (element.has("expire_on_contact")) this.expireOnImpact = element.get("expire_on_contact").getAsBoolean();

		return super.fromJson(element, parser);
	}
}