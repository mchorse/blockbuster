package mchorse.blockbuster.client.particles.components.shape;

import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;

import javax.vecmath.Vector3d;

public abstract class ShapeDirection
{
	public static final ShapeDirection INWARDS = new Inwards(1);
	public static final ShapeDirection OUTWARDS = new Inwards(-1);

	public abstract void applyDirection(BedrockParticle particle, double x, double y, double z);

	private static class Inwards extends ShapeDirection
	{
		private float factor;

		public Inwards(float factor)
		{
			this.factor = factor;
		}

		@Override
		public void applyDirection(BedrockParticle particle, double x, double y, double z)
		{
			Vector3d vector = new Vector3d(particle.position);

			vector.sub(new Vector3d(x, y, z));
			vector.normalize();

			particle.speed.set(vector);
		}
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
		public void applyDirection(BedrockParticle particle, double x, double y, double z)
		{
			particle.speed.set((float) this.x.get(), (float) this.y.get(), (float) this.z.get());
			particle.speed.normalize();
		}
	}
}
