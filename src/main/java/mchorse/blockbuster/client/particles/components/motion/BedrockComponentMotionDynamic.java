package mchorse.blockbuster.client.particles.components.motion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.molang.Molang;
import mchorse.blockbuster.client.particles.molang.MolangExpression;

public class BedrockComponentMotionDynamic extends BedrockComponentBase
{
	public MolangExpression[] motionAcceleration = {Molang.ZERO, Molang.ZERO, Molang.ZERO};
	public MolangExpression motionDrag = Molang.ZERO;
	public MolangExpression rotationAcceleration = Molang.ZERO;
	public MolangExpression rotationDrag = Molang.ZERO;

	@Override
	public BedrockComponentBase fromJson(JsonElement elem)
	{
		if (!elem.isJsonObject()) return super.fromJson(elem);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("linear_acceleration"))
		{
			JsonArray array = element.getAsJsonArray("linear_acceleration");

			if (array.size() >= 3)
			{
				this.motionAcceleration[0] = Molang.parse(array.get(0));
				this.motionAcceleration[1] = Molang.parse(array.get(1));
				this.motionAcceleration[2] = Molang.parse(array.get(2));
			}
		}

		if (element.has("linear_drag_coefficient")) this.motionDrag = Molang.parse(element.get("linear_drag_coefficient"));
		if (element.has("rotation_acceleration")) this.rotationAcceleration = Molang.parse(element.get("rotation_acceleration"));
		if (element.has("rotation_drag_coefficient")) this.rotationDrag = Molang.parse(element.get("rotation_drag_coefficient"));

		return super.fromJson(element);
	}
}