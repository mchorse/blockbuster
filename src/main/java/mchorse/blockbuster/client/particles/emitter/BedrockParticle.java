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
	public boolean relative;

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

	public float u1;
	public float v1;
	public float u2;
	public float v2;
	public float w;
	public float h;

	public void update()
	{
		this.prevRotation = this.rotation;
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;

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