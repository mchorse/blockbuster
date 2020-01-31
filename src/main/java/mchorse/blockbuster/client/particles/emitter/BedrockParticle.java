package mchorse.blockbuster.client.particles.emitter;

public class BedrockParticle
{
	public float random1 = (float) Math.random();
	public float random2 = (float) Math.random();
	public float random3 = (float) Math.random();
	public float random4 = (float) Math.random();

	public int age;
	public int lifetime;
	public boolean dead;

	public float rotation;
	public float rotationVelocity;
	public float prevRotation;

	public float x;
	public float y;
	public float z;
	public float motionX;
	public float motionY;
	public float motionZ;
	public float prevX;
	public float prevY;
	public float prevZ;

	public float r = 1;
	public float g = 1;
	public float b = 1;
	public float a = 1;
	public float prevR = 1;
	public float prevG = 1;
	public float prevB = 1;
	public float prevA = 1;

	public void update()
	{
		this.prevRotation = this.rotation;
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.prevR = this.r;
		this.prevG = this.g;
		this.prevB = this.b;
		this.prevA = this.a;

		this.rotation += this.rotationVelocity;
		this.x += this.motionX;
		this.y += this.motionY;
		this.z += this.motionZ;

		if (this.age >= this.lifetime)
		{
			this.dead = true;
		}

		this.age ++;
	}
}