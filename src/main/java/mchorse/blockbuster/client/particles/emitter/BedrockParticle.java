package mchorse.blockbuster.client.particles.emitter;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class BedrockParticle
{
	public float random1 = (float) Math.random();
	public float random2 = (float) Math.random();
	public float random3 = (float) Math.random();
	public float random4 = (float) Math.random();

	public int age;
	public int lifetime;
	public boolean dead;
	public boolean relative;
	public boolean manual;

	public float rotation;
	public float initalRotation;
	public float rotationVelocity;
	public float prevRotation;

	public Vector3d position = new Vector3d();
	public Vector3d prevPosition = new Vector3d();

	public Vector3f speed = new Vector3f();
	public Vector3f acceleration = new Vector3f();
	public float drag = 0;

	public float r = 1;
	public float g = 1;
	public float b = 1;
	public float a = 1;

	public BedrockParticle()
	{
		this.speed.set((float) Math.random() - 0.5F, (float) Math.random() - 0.5F, (float) Math.random() - 0.5F);
		this.speed.normalize();
	}

	public double getAge(float partialTick)
	{
		return (this.age + partialTick) / 20.0;
	}

	public void update()
	{
		this.prevRotation = this.rotation;
		this.prevPosition.set(this.position);

		if (!this.manual)
		{
			this.rotation += this.rotationVelocity / 20F;

			Vector3f vec = new Vector3f(this.speed);
			vec.scale(-this.drag);

			this.acceleration.add(vec);
			this.acceleration.scale(1 / 20F);
			this.speed.add(this.acceleration);

			this.position.x += this.speed.x / 20F;
			this.position.y += this.speed.y / 20F;
			this.position.z += this.speed.z / 20F;
		}

		if (this.lifetime >= 0 && this.age >= this.lifetime)
		{
			this.dead = true;
		}

		this.age ++;
	}
}