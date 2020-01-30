package mchorse.blockbuster.client.particles.components.shape;

import mchorse.blockbuster.client.particles.molang.MolangExpression;

public abstract class ShapeDirection
{
	public static final ShapeDirection INWARDS = new Inwards();
	public static final ShapeDirection OUTWARDS = new Outwards();

	public abstract void applyDirection();

	private static class Inwards extends ShapeDirection
	{
		@Override
		public void applyDirection()
		{}
	}

	private static class Outwards extends ShapeDirection
	{
		@Override
		public void applyDirection()
		{}
	}

	public static class Vector extends ShapeDirection
	{
		public MolangExpression x;
		public MolangExpression y;
		public MolangExpression z;

		public Vector(MolangExpression x, MolangExpression y, MolangExpression z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public void applyDirection()
		{}
	}
}
