package mchorse.blockbuster.client.particles.molang.expressions;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.mclib.math.Constant;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.Operation;

public abstract class MolangExpression implements IValue
{
	public MolangParser context;

	public static boolean isZero(MolangExpression expression)
	{
		return isConstant(expression, 0);
	}

	public static boolean isOne(MolangExpression expression)
	{
		return isConstant(expression, 1);
	}

	public static boolean isConstant(MolangExpression expression, double x)
	{
		if (expression instanceof MolangValue)
		{
			MolangValue value = (MolangValue) expression;

			return value.value instanceof Constant && Operation.equals(value.value.get(), x);
		}

		return false;
	}

	public static boolean isExpressionConstant(MolangExpression expression)
	{
		if (expression instanceof MolangValue)
		{
			MolangValue value = (MolangValue) expression;

			return value.value instanceof Constant;
		}

		return false;
	}

	public MolangExpression(MolangParser context)
	{
		this.context = context;
	}

	public JsonElement toJson()
	{
		return new JsonPrimitive(this.toString());
	}
}