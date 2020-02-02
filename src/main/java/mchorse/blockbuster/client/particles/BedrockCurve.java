package mchorse.blockbuster.client.particles;

import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;

public class BedrockCurve
{
	public BedrockCurveType type = BedrockCurveType.LINEAR;
	public MolangExpression[] nodes = {MolangParser.ZERO, MolangParser.ONE, MolangParser.ZERO};
	public MolangExpression input;
	public MolangExpression range;

	/* public float interpolate()
	{
		return this.interpolateCurve(this.input / this.range);
	} */

	private float interpolateCurve(float x)
	{
		return x;
	}
}