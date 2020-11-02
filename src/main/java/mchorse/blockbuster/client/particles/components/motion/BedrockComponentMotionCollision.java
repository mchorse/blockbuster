package mchorse.blockbuster.client.particles.components.motion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentParticleUpdate;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;
import mchorse.mclib.math.Operation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import java.lang.reflect.Field;
import java.util.List;

public class BedrockComponentMotionCollision extends BedrockComponentBase implements IComponentParticleUpdate
{
	public MolangExpression enabled = MolangParser.ONE;
	public float collissionDrag = 0;
	public float bounciness = 1;
	public float randomBounciness = 0;
	public int splitParticleCount;
	public float splitParticleSpeedThreshold; //threshold to activate the split
	public float radius = 0.01F;
	public boolean expireOnImpact;
	public boolean realisticCollision;
	
	/*
	 * this is used to estimate whether an object is only bouncing or lying on a surface
	 * 
	 * NOTE: doesn't always work - specifically sometimes with realistic collision. 
	 * I haven't found a solution, to stop the particles from sometimes 
	 * bouncing slightly, without changing the whole calculation...
	 */
	public Vector3f collisionTime = new Vector3f();

	/* Runtime options */
	public boolean json;
	private Vector3d previous = new Vector3d();
	private Vector3d current = new Vector3d();
	private BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

	@Override
	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject()) return super.fromJson(elem, parser);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("enabled")) this.enabled = parser.parseJson(element.get("enabled"));
		if (element.has("collision_drag")) this.collissionDrag = element.get("collision_drag").getAsFloat();
		if (element.has("coefficient_of_restitution")) this.bounciness = element.get("coefficient_of_restitution").getAsFloat();
		if (element.has("bounciness_randomness")) this.randomBounciness = element.get("bounciness_randomness").getAsFloat();
		if (element.has("split_particle_count")) this.splitParticleCount = element.get("split_particle_count").getAsInt();
		if (element.has("split_particle_speedThreshold")) this.splitParticleSpeedThreshold = element.get("split_particle_speedThreshold").getAsFloat();
		if (element.has("collision_radius")) this.radius = element.get("collision_radius").getAsFloat();
		if (element.has("expire_on_contact")) this.expireOnImpact = element.get("expire_on_contact").getAsBoolean();
		if (element.has("realisticCollision")) this.realisticCollision = element.get("realisticCollision").getAsBoolean();
		
		return super.fromJson(element, parser);
	}

	@Override
	public JsonElement toJson()
	{
		JsonObject object = new JsonObject();

		if (MolangExpression.isZero(this.enabled))
		{
			return object;
		}

		if (!MolangExpression.isOne(this.enabled)) object.add("enabled", this.enabled.toJson());
		if (this.realisticCollision) object.addProperty("realisticCollision", true);
		if (this.collissionDrag != 0) object.addProperty("collision_drag", this.collissionDrag);
		if (this.bounciness != 1) object.addProperty("coefficient_of_restitution", this.bounciness);
		if (this.randomBounciness != 0) object.addProperty("bounciness_randomness", this.randomBounciness);
		if (this.splitParticleCount != 0) object.addProperty("split_particle_count", this.splitParticleCount);
		if (this.splitParticleSpeedThreshold != 0) object.addProperty("split_particle_speedThreshold", this.splitParticleSpeedThreshold);
		if (this.radius != 0.01F) object.addProperty("collision_radius", this.radius);
		if (this.expireOnImpact) object.addProperty("expire_on_contact", true);

		return object;
	}

	@Override
	public void update(BedrockEmitter emitter, BedrockParticle particle)
	{
		if (emitter.world == null)
		{
			return;
		}

		if (!particle.manual && !Operation.equals(this.enabled.get(), 0))
		{
			float r = this.radius;

			this.previous.set(particle.getGlobalPosition(emitter, particle.prevPosition));
			this.current.set(particle.getGlobalPosition(emitter));

			Vector3d prev = this.previous;
			Vector3d now = this.current;

			double x = now.x - prev.x;
			double y = now.y - prev.y;
			double z = now.z - prev.z;
			boolean veryBig = Math.abs(x) > 10 || Math.abs(y) > 10 || Math.abs(z) > 10;

			this.pos.setPos(now.x, now.y, now.z);

			if (veryBig || !emitter.world.isBlockLoaded(this.pos))
			{
				return;
			}

			AxisAlignedBB aabb = new AxisAlignedBB(prev.x - r, prev.y - r, prev.z - r, prev.x + r, prev.y + r, prev.z + r);

			double d0 = y;
			double origX = x;
			double origZ = z;

			List<AxisAlignedBB> list = emitter.world.getCollisionBoxes(null, aabb.expand(x, y, z));

			for (AxisAlignedBB axisalignedbb : list)
			{
				y = axisalignedbb.calculateYOffset(aabb, y);
			}

			aabb = aabb.offset(0.0D, y, 0.0D);

			for (AxisAlignedBB axisalignedbb1 : list)
			{
				x = axisalignedbb1.calculateXOffset(aabb, x);
			}

			aabb = aabb.offset(x, 0.0D, 0.0D);

			for (AxisAlignedBB axisalignedbb2 : list)
			{
				z = axisalignedbb2.calculateZOffset(aabb, z);
			}

			aabb = aabb.offset(0.0D, 0.0D, z);

			if (d0 != y || origX != x || origZ != z)
			{
				if (this.expireOnImpact)
				{
					particle.dead = true;

					return;
				}

				if (particle.relativePosition)
				{
					particle.relativePosition = false;
					particle.prevPosition.set(prev);
				}

				now.set(aabb.minX + r, aabb.minY + r, aabb.minZ + r);
				
				if (d0 != y)
				{
					if(realisticCollision && this.bounciness!=0) {
						if(this.collisionTime.y!=(particle.age-1)) particle.speed.y = -particle.speed.y*this.bounciness;
					}
					else particle.accelerationFactor.y *= -this.bounciness;
					
					if(this.randomBounciness!=0 && Math.round(particle.speed.y)!=0) {
						if(this.collisionTime.y!=(particle.age-1)) particle.speed = randomBounciness(particle.speed, 'y');
					}
					
					if(this.splitParticleCount!=0 && this.collisionTime.y!=(particle.age-1) && particle.speed.length()>0.75) 
					{
						for(int i = 0; i<splitParticleCount;i++) {
							BedrockParticle splitParticle = emitter.createParticle(false);
							
							Field[] fields = particle.getClass().getFields();
							for(int b = 0; b<fields.length; b++) {
								try {
									fields[i].set(splitParticle, fields[i].getFloat(particle));
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								}
							}
							splitParticle.position.y += d0 < y ? splitParticle.r : -splitParticle.r;
							emitter.splitParticles.add(splitParticle);
						}
						particle.dead = true;
					}
					
					this.collisionTime.y = particle.age;
					now.y += d0 < y ? r : -r;
				}
				
				if (origX != x)
				{
					if(realisticCollision && this.bounciness!=0) {
						if(this.collisionTime.x!=(particle.age-1)) particle.speed.x = -particle.speed.x*this.bounciness;
					}
					else particle.accelerationFactor.x *= -this.bounciness;
					
					if(this.randomBounciness!=0 && Math.round(particle.speed.x)!=0) {
						if(this.collisionTime.x!=(particle.age-1)) particle.speed = randomBounciness(particle.speed, 'x');
					}
					
					this.collisionTime.x = particle.age;
					now.x += origX < x ? r : -r;
				}

				if (origZ != z)
				{
					if(realisticCollision && this.bounciness!=0) {
						if(this.collisionTime.z!=(particle.age-1)) particle.speed.z = -particle.speed.z*this.bounciness;
					}
					else particle.accelerationFactor.z *= -this.bounciness;
					
					if(this.randomBounciness!=0 && Math.round(particle.speed.z)!=0) {
						if(this.collisionTime.z!=(particle.age-1)) particle.speed = randomBounciness(particle.speed, 'z');
					}
					
					this.collisionTime.z = particle.age;
					now.z += origZ < z ? r : -r;
				}

				particle.position.set(now);
				/*only apply drag when speed is almost not zero and randombounciness and realisticCollision are off
				prevent particles from accelerating away when randomBounciness is active*/
				if(!( (this.randomBounciness!=0 || this.realisticCollision) && Math.round(particle.speed.length())==0 )) 
					particle.dragFactor += this.collissionDrag;
			}
		}
	}
	
	public Vector3f randomBounciness(Vector3f vector0, char component) {
		if(this.randomBounciness!=0 && (component=='x' || component=='y' || component=='z')) {
			Vector3f vector = new Vector3f(vector0);
			float randomfactor = 0.25f; //scale down the vector components not involved in the collision reflection
			float prevLength = vector.length();
			float max = this.randomBounciness*0.1f; //scaled down to 1/10
			float min = -max;
			float random1 = (float) Math.random()*max;
			float random2 = (float) Math.random()*(max*randomfactor-min*randomfactor)+min*randomfactor;
			float random3 = (float) Math.random()*(max*randomfactor-min*randomfactor)+min*randomfactor;
			//NOTE: maybe add a tmp variable for the case bounciness==0 so the vector will be scaled back correctly
			switch(component) {
				case 'x':
					if(bounciness!=0) vector.x += vector.x<0 ? -random1 : random1;
					vector.y += random2;
					vector.z += random3;
					break;
				case 'y':
					if(bounciness!=0) vector.y += vector.y<0 ? -random1 : random1;
					vector.x += random2;
					vector.z += random3;
					break;
				case 'z':
					if(bounciness!=0) vector.z += vector.z<0 ? -random1 : random1;
					vector.y += random2;
					vector.x += random3;
					break;
			}
			vector.scale(prevLength/vector.length()); //scale back to original length
			return vector;
		}
		else if(!(component=='x' || component=='y' || component=='z')) { //component wrong input
			throw new IllegalArgumentException("Invalid component input value: "+component);
		}
		return null;
	}

	@Override
	public int getSortingIndex()
	{
		return 50;
	}
}
