package mchorse.blockbuster.client.particles.molang.expressions;

import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.mclib.math.IValue;

public abstract class MolangExpression implements IValue
{
	public MolangParser context;

	public MolangExpression(MolangParser context)
	{
		this.context = context;
	}
}
