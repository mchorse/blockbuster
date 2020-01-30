package mchorse.blockbuster.client.particles.components.appearance;

import mchorse.blockbuster.client.particles.molang.MolangExpression;

import java.util.List;

public abstract class Color
{
	public abstract int compute();

	public static class Static extends Color
	{
		public int color;

		public Static(int color)
		{
			this.color = color;
		}

		@Override
		public int compute()
		{
			return this.color;
		}
	}

	public static class Gradient extends Color
	{
		public List<ColorStop> stops;
		public MolangExpression interpolant;

		public Gradient(List<ColorStop> stops, MolangExpression interpolant)
		{
			this.stops = stops;
			this.interpolant = interpolant;
		}

		@Override
		public int compute()
		{
			return 0; // TODO: implement
		}

		public static class ColorStop
		{
			public float stop;
			public int color;

			public ColorStop(float stop, int color)
			{
				this.stop = stop;
				this.color = color;
			}
		}
	}
}