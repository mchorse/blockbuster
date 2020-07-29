package mchorse.blockbuster.client.particles.emitter;

import mchorse.mclib.utils.Interpolations;

import javax.vecmath.Matrix3f;
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
	public boolean relativePosition;
	public boolean relativeRotation;
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
	public Matrix3f matrix = new Matrix3f();
	private boolean matrixSet;

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
		this.matrix.setIdentity();
	}

	public double getDistanceSq(BedrockEmitter emitter)
	{
		Vector3d pos = this.getGlobalPosition(emitter);

		double dx = emitter.cX - pos.x;
		double dy = emitter.cY - pos.y;
		double dz = emitter.cZ - pos.z;

		return dx * dx + dy * dy + dz * dz;
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
		double px = vector.x;
		double py = vector.y;
		double pz = vector.z;

		if (this.relativePosition && this.relativeRotation)
		{
			Vector3f v = new Vector3f((float) px, (float) py, (float) pz);
			emitter.rotation.transform(v);

			px = v.x;
			py = v.y;
			pz = v.z;

			px += emitter.lastGlobal.x;
			py += emitter.lastGlobal.y;
			pz += emitter.lastGlobal.z;
		}

		this.global.set(px, py, pz);

		return this.global;
	}

	public void update(BedrockEmitter emitter)
	{
		this.prevRotation = this.rotation;
		this.prevPosition.set(this.position);

		this.setupMatrix(emitter);

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

			if (this.relativePosition || this.relativeRotation)
			{
				this.matrix.transform(vec);
			}

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

	public void setupMatrix(BedrockEmitter emitter)
	{
		if (this.relativePosition)
		{
			if (this.relativeRotation)
			{
				this.matrix.setIdentity();
			}
			else if (!this.matrixSet)
			{
				this.matrix.set(emitter.rotation);
				this.matrixSet = true;
			}
		}
		else if (this.relativeRotation)
		{
			this.matrix.set(emitter.rotation);
		}
	}
}