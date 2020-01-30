package mchorse.blockbuster.client.particles;

import mchorse.blockbuster.client.particles.molang.Molang;
import mchorse.blockbuster.client.particles.molang.MolangExpression;

public class BedrockCurve
{
	public BedrockCurveType type = BedrockCurveType.LINEAR;
	public MolangExpression[] nodes = {Molang.ZERO, Molang.ONE, Molang.ZERO};
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