package mchorse.blockbuster.client.particles.molang;

import com.google.gson.JsonElement;

public class Molang
{
	public static final MolangExpression ZERO = new MolangConstant(0);
	public static final MolangExpression ONE = new MolangConstant(1);

	public static MolangExpression parse(JsonElement element)
	{
		return ZERO;
	}
}