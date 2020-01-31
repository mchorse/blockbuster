package mchorse.blockbuster.client.particles.molang;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class Molang
{
	public static final MolangExpression ZERO = new MolangConstant(0);
	public static final MolangExpression ONE = new MolangConstant(1);

	public static MolangExpression parse(JsonElement element)
	{
		if (element.isJsonPrimitive())
		{
			JsonPrimitive primitive = element.getAsJsonPrimitive();

			if (primitive.isString())
			{
				/* TODO: Parse expression */
			}
			else
			{
				return new MolangConstant(primitive.getAsFloat());
			}
		}

		return ZERO;
	}
}