package mchorse.blockbuster.client.particles.components.appearance;

import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;

import java.util.List;

public abstract class Color
{
	public abstract void compute(BedrockParticle particle);

	public static class Solid extends Color
	{
		public MolangExpression r = MolangParser.ONE;
		public MolangExpression g = MolangParser.ONE;
		public MolangExpression b = MolangParser.ONE;
		public MolangExpression a = MolangParser.ONE;

		public Solid(MolangExpression r, MolangExpression g, MolangExpression b, MolangExpression a)
		{
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
		}

		@Override
		public void compute(BedrockParticle particle)
		{
			particle.r = (float) this.r.get();
			particle.g = (float) this.g.get();
			particle.b = (float) this.b.get();
			particle.a = (float) this.a.get();
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
		public void compute(BedrockParticle particle)
		{
			/* TODO: implement */
		}

		public static class ColorStop
		{
			public float stop;
			public Color.Solid color;

			public ColorStop(float stop, Color.Solid color)
			{
				this.stop = stop;
				this.color = color;
			}
		}
	}
}