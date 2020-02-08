package mchorse.blockbuster.client.particles.molang.expressions;

import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.mclib.math.IValue;

public class MolangValue extends MolangExpression
{
	public IValue value;

	public MolangValue(MolangParser context, IValue value)
	{
		super(context);

		this.value = value;
	}

	@Override
	public double get()
	{
		return (float) this.value.get();
	}
}