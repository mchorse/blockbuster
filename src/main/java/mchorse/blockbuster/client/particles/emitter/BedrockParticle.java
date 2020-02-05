package mchorse.blockbuster.client.particles.emitter;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class BedrockParticle
{
	/* Randoms */
	public float random1 = (float) Math.random();
	public float random2 = (float) Math.random();
	public float random3 = (float) Math.random();
	public float random4 = (float) Math.random();

	/* States */
	public int age;
	public int lifetime;
	public boolean dead;
	public boolean relative;
	public boolean manual;

	/* Rotation */
	public float rotation;
	public float initialRotation;
	public float prevRotation;

	public float rotationVelocity;
	public float rotationAcceleration;
	public float rotationDrag;

	/* Position */
	public Vector3d position = new Vector3d();
	public Vector3d initialPosition = new Vector3d();
	public Vector3d prevPosition = new Vector3d();

	public Vector3f speed = new Vector3f();
	public Vector3f acceleration = new Vector3f();
	public Vector3f accelerationFactor = new Vector3f(1, 1, 1);
	public float drag = 0;
	public float dragFactor = 0;

	/* Color */
	public float r = 1;
	public float g = 1;
	public float b = 1;
	public float a = 1;

	private Vector3d global = new Vector3d();

	public BedrockParticle()
	{
		this.speed.set((float) Math.random() - 0.5F, (float) Math.random() - 0.5F, (float) Math.random() - 0.5F);
		this.speed.normalize();
	}

	public double getAge(float partialTick)
	{
		return (this.age + partialTick) / 20.0;
	}

	public Vector3d getGlobalPosition(BedrockEmitter emitter)
	{
		return this.getGlobalPosition(emitter, this.position);
	}

	public Vector3d getGlobalPosition(BedrockEmitter emitter, Vector3d vector)
	{
		this.global.set(vector);

		if (this.relative)
		{
			this.global.add(emitter.lastGlobal);
		}

		return this.global;
	}

	public void update()
	{
		this.prevRotation = this.rotation;
		this.prevPosition.set(this.position);

		if (!this.manual)
		{
			float rotationAcceleration = this.rotationAcceleration / 20F -this.rotationDrag * this.rotationVelocity;
			this.rotationVelocity += rotationAcceleration / 20F;
			this.rotation = this.initialRotation + this.rotationVelocity * this.age;

			/* Position */
			Vector3f vec = new Vector3f(this.speed);
			vec.scale(-(this.drag + this.dragFactor));

			this.acceleration.add(vec);
			this.acceleration.scale(1 / 20F);
			this.speed.add(this.acceleration);

			vec.set(this.speed);
			vec.x *= this.accelerationFactor.x;
			vec.y *= this.accelerationFactor.y;
			vec.z *= this.accelerationFactor.z;

			this.position.x += vec.x / 20F;
			this.position.y += vec.y / 20F;
			this.position.z += vec.z / 20F;
		}

		if (this.lifetime >= 0 && this.age >= this.lifetime)
		{
			this.dead = true;
		}

		this.age ++;
	}
}