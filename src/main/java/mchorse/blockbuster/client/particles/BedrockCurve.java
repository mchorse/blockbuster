package mchorse.blockbuster.client.particles;

import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;
import mchorse.mclib.math.Variable;
import mchorse.mclib.utils.Interpolations;

public class BedrockCurve
{
	public BedrockCurveType type = BedrockCurveType.LINEAR;
	public MolangExpression[] nodes = {MolangParser.ZERO, MolangParser.ONE, MolangParser.ZERO};
	public MolangExpression input;
	public MolangExpression range;
	public Variable variable;

	public double compute()
	{
		return this.computeCurve(this.input.get() / this.range.get());
	}

	private double computeCurve(double factor)
	{
		int length = this.nodes.length;

		if (length == 0)
		{
			return 0;
		}
		else if (length == 1 || factor < 0)
		{
			return this.nodes[0].get();
		}
		else if (factor > 1)
		{
			return this.nodes[length - 1].get();
		}

		factor *= length;
		int index = (int) factor;

		if (this.type == BedrockCurveType.HERMITE)
		{
			MolangExpression beforeFirst = this.getNode(index - 1);
			MolangExpression first = this.getNode(index);
			MolangExpression next = this.getNode(index + 1);
			MolangExpression afterNext = this.getNode(index + 2);

			return Interpolations.cubicHermite(beforeFirst.get(), first.get(), next.get(), afterNext.get(), factor % 1);
		}

		MolangExpression first = this.getNode(index);
		MolangExpression next = this.getNode(index + 1);

		return Interpolations.lerp(first.get(), next.get(), factor % 1);
	}

	private MolangExpression getNode(int index)
	{
		if (index < 0)
		{
			return this.nodes[0];
		}
		else if (index >= this.nodes.length)
		{
			return this.nodes[this.nodes.length - 1];
		}

		return this.nodes[index];
	}
}