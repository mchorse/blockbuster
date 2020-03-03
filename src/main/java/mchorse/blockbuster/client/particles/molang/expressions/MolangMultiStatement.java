package mchorse.blockbuster.client.particles.molang.expressions;

import mchorse.blockbuster.client.particles.molang.MolangParser;

import java.util.ArrayList;
import java.util.List;

public class MolangMultiStatement extends MolangExpression
{
	public List<MolangExpression> expressions = new ArrayList<MolangExpression>();

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
		}

		return (float) value;
	}
}