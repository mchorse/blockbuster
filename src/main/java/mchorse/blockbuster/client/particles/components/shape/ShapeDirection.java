package mchorse.blockbuster.client.particles.components.shape;

import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.MolangExpression;

import javax.vecmath.Vector3f;

public abstract class ShapeDirection
{
	public static final ShapeDirection INWARDS = new Inwards(1);
	public static final ShapeDirection OUTWARDS = new Inwards(-1);

	public abstract void applyDirection(BedrockParticle particle, float x, float y, float z);

	private static class Inwards extends ShapeDirection
	{
		private float factor;

		public Inwards(float factor)
		{
			this.factor = factor;
		}

		@Override
		public void applyDirection(BedrockParticle particle, float x, float y, float z)
		{
			Vector3f vector = new Vector3f(x, y, z);

			vector.sub(new Vector3f(particle.x, particle.y, particle.z));
			vector.normalize();

			particle.motionX = vector.x * this.factor;
			particle.motionY = vector.y * this.factor;
			particle.motionZ = vector.z * this.factor;
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
		public void applyDirection(BedrockParticle particle, float x, float y, float z)
		{
			particle.motionX = this.x.evaluate();
			particle.motionY = this.y.evaluate();
			particle.motionZ = this.z.evaluate();
		}
	}
}
