package mchorse.blockbuster.client.particles.molang.expressions;

import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.mclib.math.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class MolangMultiStatement extends MolangExpression
{
	public List<MolangExpression> expressions = new ArrayList<MolangExpression>();
	public Map<String, Variable> locals = new HashMap<String, Variable>();

	public MolangMultiStatement(MolangParser context)
	{
		super(context);
	}

	@Override
	public double get()
	{
		double value = 0;

		for (MolangExpression expression : this.expressions)
		{
			value = expression.get();

			if (expression instanceof MolangValue && ((MolangValue) expression).returns)
			{
				break;
			}
		}

		return value;
	}

	@Override
	public String toString()
	{
		StringJoiner builder = new StringJoiner("; ");

		for (MolangExpression expression : this.expressions)
		{
			builder.add(expression.toString());
		}

		return builder.toString();
	}
}