package mchorse.blockbuster.client.particles.components.appearance;

import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;
import mchorse.mclib.utils.Interpolations;

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

		public void lerp(BedrockParticle particle, float factor)
		{
			particle.r = Interpolations.lerp(particle.r, (float) this.r.get(), factor);
			particle.g = Interpolations.lerp(particle.r, (float) this.g.get(), factor);
			particle.b = Interpolations.lerp(particle.r, (float) this.b.get(), factor);
			particle.a = Interpolations.lerp(particle.r, (float) this.a.get(), factor);
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
			int length = this.stops.size();

			if (length == 0)
			{
				particle.r = particle.g = particle.b = particle.a = 1;

				return;
			}
			else if (length == 1)
			{
				this.stops.get(0).color.compute(particle);

				return;
			}

			double factor = this.interpolant.get();

			for (int i = 0; i < length; i ++)
			{
				ColorStop stop = this.stops.get(i);

				if (stop.stop < factor)
				{
					stop.color.compute(particle);

					if (i < length - 1)
					{
						ColorStop next = this.stops.get(i + 1);

						next.color.lerp(particle, (float) (factor - stop.stop) / (next.stop - stop.stop));

						return;
					}
				}
			}
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