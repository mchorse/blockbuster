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
	public float randomDamp = 0;
	public float damp = 0; //should be like in Blender
	public int splitParticleCount;
	public float splitParticleSpeedThreshold; //threshold to activate the split
	public float radius = 0.01F;
	public boolean expireOnImpact;
	public boolean realisticCollision;
	
	

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
		if (element.has("damp")) this.damp = element.get("damp").getAsFloat();
		if (element.has("random_damp")) this.randomDamp = element.get("random_damp").getAsFloat();
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
		if (this.damp != 0) object.addProperty("damp", this.damp);
		if (this.randomDamp != 0) object.addProperty("random_damp", this.randomDamp);
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
					/*realistic collision*/
					if(realisticCollision) 
					{
						if(particle.collisionTime.y!=(particle.age-1)) 
						{
							if(this.bounciness!=0) particle.speed.y = -particle.speed.y*this.bounciness;
						}
						else if(particle.collisionTime.y==(particle.age-1)) particle.speed.y = 0;
					}
					else particle.accelerationFactor.y *= -this.bounciness;
					
					if(particle.collisionTime.y!=(particle.age-1))
					{
						/*random bounciness*/
						if(this.randomBounciness!=0 /*&& Math.round(particle.speed.y)!=0*/) 
						{
							particle.speed = randomBounciness(particle.speed, 'y', this.randomBounciness);
						}
						
						/*split particles*/
						if(this.splitParticleCount!=0) 
						{
							splitParticle(particle, emitter, 'y', d0, y, now, prev);
						}
						
						/*damping*/
						if(damp!=0) 
						{
							particle.speed = damping(particle.speed);
						}
					}
					
					
					particle.collisionTime.y = particle.age;
					now.y += d0 < y ? r : -r;
				}
				
				if (origX != x)
				{
					/*realistic collision*/
					if(realisticCollision) 
					{
						if(particle.collisionTime.x!=(particle.age-1)) 
						{
							if(this.bounciness!=0) particle.speed.x = -particle.speed.x*this.bounciness;
						}
						else if(particle.collisionTime.x==(particle.age-1)) particle.speed.x = 0;
					}
					else particle.accelerationFactor.x *= -this.bounciness;
					
					if(particle.collisionTime.x!=(particle.age-1))
					{
						/*random bounciness*/
						if(this.randomBounciness!=0 /*&& Math.round(particle.speed.x)!=0*/) 
						{
							particle.speed = randomBounciness(particle.speed, 'x', this.randomBounciness);
						}
						
						/*split particles*/
						if(this.splitParticleCount!=0) 
						{
							splitParticle(particle, emitter, 'x', origX, x, now, prev);
						}
						
						/*damping*/
						if(damp!=0) 
						{
							particle.speed = damping(particle.speed);
						}
					}
					
					particle.collisionTime.x = particle.age;
					now.x += origX < x ? r : -r;
				}

				if (origZ != z)
				{
					/*realistic collision*/
					if(realisticCollision) 
					{
						if(particle.collisionTime.z!=(particle.age-1)) 
						{
							if(this.bounciness!=0) particle.speed.z = -particle.speed.z*this.bounciness;
						}
						else if(particle.collisionTime.z==(particle.age-1)) particle.speed.z = 0;
					}
					else particle.accelerationFactor.z *= -this.bounciness;
					
					if(particle.collisionTime.z!=(particle.age-1))
					{
						/*random bounciness*/
						if(this.randomBounciness!=0 /*&& Math.round(particle.speed.z)!=0*/) 
						{
							particle.speed = randomBounciness(particle.speed, 'z', this.randomBounciness);
						}
						
						/*split particles*/
						if(this.splitParticleCount!=0) 
						{
							splitParticle(particle, emitter, 'z', origZ, z, now, prev);
						}
						
						/*damping*/
						if(damp!=0) 
						{
							particle.speed = damping(particle.speed);
						}
					}
					
					particle.collisionTime.z = particle.age;
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
	
	public Vector3f damping(Vector3f vector) 
	{
		float randomDamp = (this.randomDamp*0.1f)/2;
		float random = (float) (randomDamp*(Math.random()*2-1));
		float clampedValue = Math.max(0, Math.min(1, (1-damp)+random)); //clamp between 0 and 1
		vector.scale(clampedValue);
		return vector;
	}
	
	public void splitParticle(BedrockParticle particle, BedrockEmitter emitter, char component, double orig, double offset, Vector3d now, Vector3d prev) {
		if(component=='x' || component=='y' || component=='z')
		{
			for(int i = 0; i<splitParticleCount;i++) 
			{
				BedrockParticle splitParticle = emitter.createParticle(false);
				splitParticle.initialPosition.set(particle.initialPosition);
				splitParticle.collisionTime.set(particle.collisionTime);
				splitParticle.position.set(now);
				splitParticle.prevPosition.set(prev);
				
				//Mh is this necessary?
				splitParticle.acceleration.set(particle.acceleration);
				splitParticle.accelerationFactor.set(particle.accelerationFactor);
				splitParticle.drag = particle.drag;
				splitParticle.dragFactor = particle.dragFactor;
				
				
				splitParticle.age = particle.age;
				
				switch(component) 
				{
					case 'x':
						if(!(Math.abs(particle.speed.x)>Math.abs(this.splitParticleSpeedThreshold)))
							return;
						splitParticle.collisionTime.x = particle.age;
						splitParticle.position.x += orig < offset ? this.radius : -this.radius;
						break;
					case 'y':
						if(!(Math.abs(particle.speed.y)>Math.abs(this.splitParticleSpeedThreshold)))
							return;
						splitParticle.collisionTime.y = particle.age;
						splitParticle.position.y += orig < offset ? this.radius : -this.radius;
						break;
					case 'z':
						if(!(Math.abs(particle.speed.z)>Math.abs(this.splitParticleSpeedThreshold)))
							return;
						splitParticle.collisionTime.z = particle.age;
						splitParticle.position.z += orig < offset ? this.radius : -this.radius;
						break;
				}
				Vector3f randomSpeed = randomBounciness(particle.speed, component, (this.randomBounciness!=0) ? this.randomBounciness : 100);
				randomSpeed.scale(1.0f/this.splitParticleCount);
				splitParticle.speed.set(randomSpeed);
				if(damp!=0) {
					splitParticle.speed = damping(splitParticle.speed);
				}
				emitter.splitParticles.add(splitParticle);
			}
			particle.dead = true;
		}
		else 
		{
			throw new IllegalArgumentException("Invalid component input value: "+component);
		}
	}
	
	public Vector3f randomBounciness(Vector3f vector0, char component, float randomness) {
		if(this.randomBounciness!=0 && (component=='x' || component=='y' || component=='z')) {
			Vector3f vector = new Vector3f(vector0);
			float randomfactor = 0.25f; //scale down the vector components not involved in the collision reflection
			float prevLength = vector.length();
			randomness *= 0.1f; //scaled down to 1/10
			float random1 = (float) Math.random()*randomness;
			float random2 = (float) (randomness*randomfactor*(Math.random()*2-1));
			float random3 = (float) (randomness*randomfactor*(Math.random()*2-1));
			/* tmpComponent explanation
			*  if bounciness=0 then the speed of a specific component wont't affect the particles movement
			*  so the particles speed needs to be scaled back without taking that component into account
			*/
			switch(component) {
				case 'x':
					vector.y += random2;
					vector.z += random3;
					if(bounciness!=0) vector.x += vector.x<0 ? -random1 : random1;
					else {
						float tmpComponent=vector.x;
						vector.x = 0;
						vector.scale(prevLength/vector.length());
						vector.x = tmpComponent;
					}
					break;
				case 'y':
					vector.x += random2;
					vector.z += random3;
					if(bounciness!=0) vector.y += vector.y<0 ? -random1 : random1;
					else {
						float tmpComponent=vector.y;
						vector.y = 0;
						vector.scale(prevLength/vector.length());
						vector.y = tmpComponent;
					}
					break;
				case 'z':
					vector.y += random2;
					vector.x += random3;
					if(bounciness!=0) vector.z += vector.z<0 ? -random1 : random1;
					else {
						float tmpComponent=vector.z;
						vector.z = 0;
						vector.scale(prevLength/vector.length());
						vector.z = tmpComponent;
					}
					break;
			}
			if(bounciness!=0) vector.scale(prevLength/vector.length()); //scale back to original length
			return vector;
		}
		else if(!(component=='x' || component=='y' || component=='z')) { //component wrong input
			throw new IllegalArgumentException("Invalid component input value: "+component);
		}
		return vector0;
	}

	@Override
	public int getSortingIndex()
	{
		return 50;
	}
}
