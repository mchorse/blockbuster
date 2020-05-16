package mchorse.blockbuster.client.particles.components.motion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentParticleUpdate;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;

public class BedrockComponentMotionDynamic extends BedrockComponentMotion implements IComponentParticleUpdate
{
	public MolangExpression[] motionAcceleration = {MolangParser.ZERO, MolangParser.ZERO, MolangParser.ZERO};
	public MolangExpression motionDrag = MolangParser.ZERO;
	public MolangExpression rotationAcceleration = MolangParser.ZERO;
	public MolangExpression rotationDrag = MolangParser.ZERO;

	@Override
	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject()) return super.fromJson(elem, parser);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("linear_acceleration"))
		{
			JsonArray array = element.getAsJsonArray("linear_acceleration");

			if (array.size() >= 3)
			{
				this.motionAcceleration[0] = parser.parseJson(array.get(0));
				this.motionAcceleration[1] = parser.parseJson(array.get(1));
				this.motionAcceleration[2] = parser.parseJson(array.get(2));
			}
		}

		if (element.has("linear_drag_coefficient")) this.motionDrag = parser.parseJson(element.get("linear_drag_coefficient"));
		if (element.has("rotation_acceleration")) this.rotationAcceleration = parser.parseJson(element.get("rotation_acceleration"));
		if (element.has("rotation_drag_coefficient")) this.rotationDrag = parser.parseJson(element.get("rotation_drag_coefficient"));

		return super.fromJson(element, parser);
	}

	@Override
	public JsonElement toJson()
	{
		JsonObject object = new JsonObject();
		JsonArray acceleration = new JsonArray();

		for (MolangExpression expression : this.motionAcceleration)
		{
			acceleration.add(expression.toJson());
		}

		object.add("linear_acceleration", acceleration);

		if (!MolangExpression.isZero(this.motionDrag)) object.add("linear_drag_coefficient", this.motionDrag.toJson());
		if (!MolangExpression.isZero(this.rotationAcceleration)) object.add("rotation_acceleration", this.rotationAcceleration.toJson());
		if (!MolangExpression.isZero(this.rotationDrag)) object.add("rotation_drag_coefficient", this.rotationDrag.toJson());

		return object;
	}

	@Override
	public void update(BedrockEmitter emitter, BedrockParticle particle)
	{
		particle.acceleration.x += (float) this.motionAcceleration[0].get();
		particle.acceleration.y += (float) this.motionAcceleration[1].get();
		particle.acceleration.z += (float) this.motionAcceleration[2].get();
		particle.drag = (float) this.motionDrag.get();

		particle.rotationAcceleration += (float) this.rotationAcceleration.get() / 20F;
		particle.rotationDrag = (float) this.rotationDrag.get();
	}
}