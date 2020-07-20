package mchorse.blockbuster.client.particles.components.shape;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;

import javax.vecmath.Vector3d;

public abstract class ShapeDirection
{
	public static final ShapeDirection INWARDS = new Inwards(-1);
	public static final ShapeDirection OUTWARDS = new Inwards(1);

	public abstract void applyDirection(BedrockParticle particle, double x, double y, double z);

	public abstract JsonElement toJson();

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

			if (vector.length() <= 0)
			{
				vector.set(0, 0, 0);
			}
			else
			{
				vector.normalize();
				vector.scale(this.factor);
			}

			particle.speed.set(vector);
		}

		@Override
		public JsonElement toJson()
		{
			return new JsonPrimitive(this.factor < 0 ? "inwards" : "outwards");
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

			if (particle.speed.length() <= 0)
			{
				particle.speed.set(0, 0, 0);
			}
			else
			{
				particle.speed.normalize();
			}
		}

		@Override
		public JsonElement toJson()
		{
			JsonArray array = new JsonArray();

			array.add(this.x.toJson());
			array.add(this.y.toJson());
			array.add(this.z.toJson());

			return array;
		}
	}
}
