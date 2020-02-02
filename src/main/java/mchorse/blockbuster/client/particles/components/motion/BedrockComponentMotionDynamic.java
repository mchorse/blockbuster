package mchorse.blockbuster.client.particles.components.motion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;

public class BedrockComponentMotionDynamic extends BedrockComponentBase
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
}