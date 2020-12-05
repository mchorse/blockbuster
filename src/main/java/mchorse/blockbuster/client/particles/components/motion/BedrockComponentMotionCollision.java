package mchorse.blockbuster.client.particles.components.motion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentParticleUpdate;
import mchorse.blockbuster.client.particles.components.appearance.BedrockComponentCollisionAppearance;
import mchorse.blockbuster.client.particles.components.appearance.BedrockComponentCollisionTinting;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.mclib.math.Operation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import java.lang.reflect.Field;
import java.util.List;

public class BedrockComponentMotionCollision extends BedrockComponentBase implements IComponentParticleUpdate
{
	public MolangExpression enabled = MolangParser.ONE;
	public MolangExpression preserveEnergy = MolangParser.ZERO;
	public boolean entityCollision;
	public boolean momentum;
	public float collissionDrag = 0;
	public float bounciness = 1;
	public float randomBounciness = 0;
	public float randomDamp = 0;
	public float damp = 0; //should be like in Blender
	public int splitParticleCount;
	public float splitParticleSpeedThreshold; //threshold to activate the split
	public float radius = 0.01F;
	public boolean expireOnImpact;
	public int expirationDelay;
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
		if (element.has("entityCollision")) this.entityCollision = element.get("entityCollision").getAsBoolean();
		if (element.has("momentum")) this.momentum = element.get("momentum").getAsBoolean();
		if (element.has("collision_drag")) this.collissionDrag = element.get("collision_drag").getAsFloat();
		if (element.has("coefficient_of_restitution")) this.bounciness = element.get("coefficient_of_restitution").getAsFloat();
		if (element.has("bounciness_randomness")) this.randomBounciness = element.get("bounciness_randomness").getAsFloat();
		if (element.has("preserveEnergy")) this.preserveEnergy = parser.parseJson(element.get("preserveEnergy"));
		if (element.has("damp")) this.damp = element.get("damp").getAsFloat();
		if (element.has("random_damp")) this.randomDamp = element.get("random_damp").getAsFloat();
		if (element.has("split_particle_count")) this.splitParticleCount = element.get("split_particle_count").getAsInt();
		if (element.has("split_particle_speedThreshold")) this.splitParticleSpeedThreshold = element.get("split_particle_speedThreshold").getAsFloat();
		if (element.has("collision_radius")) this.radius = element.get("collision_radius").getAsFloat();
		if (element.has("expire_on_contact")) this.expireOnImpact = element.get("expire_on_contact").getAsBoolean();
		if (element.has("expirationDelay")) this.expirationDelay = element.get("expirationDelay").getAsInt();
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
		if (this.entityCollision) object.addProperty("entityCollision", true);
		if (this.momentum) object.addProperty("momentum", true);
		if (this.collissionDrag != 0) object.addProperty("collision_drag", this.collissionDrag);
		if (this.bounciness != 1) object.addProperty("coefficient_of_restitution", this.bounciness);
		if (this.randomBounciness != 0) object.addProperty("bounciness_randomness", this.randomBounciness);
		if (MolangExpression.isOne(this.preserveEnergy)) object.add("preserveEnergy", this.preserveEnergy.toJson());
		if (this.damp != 0) object.addProperty("damp", this.damp);
		if (this.randomDamp != 0) object.addProperty("random_damp", this.randomDamp);
		if (this.splitParticleCount != 0) object.addProperty("split_particle_count", this.splitParticleCount);
		if (this.splitParticleSpeedThreshold != 0) object.addProperty("split_particle_speedThreshold", this.splitParticleSpeedThreshold);
		if (this.radius != 0.01F) object.addProperty("collision_radius", this.radius);
		if (this.expireOnImpact) object.addProperty("expire_on_contact", true);
		if (this.expirationDelay!=0) object.addProperty("expirationDelay", this.expirationDelay);
		
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
			List<Entity> list2 = emitter.world.getEntitiesWithinAABB(Entity.class, aabb.expand(x, y, z));
			List<AxisAlignedBB> list = emitter.world.getCollisionBoxes(null, aabb.expand(x, y, z));

			if(this.entityCollision) 
			{
				for(Entity entity : list2) 
				{
					AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();
					list.add(axisalignedbb);
				}
			}
			
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
				if(MolangExpression.isOne(emitter.scheme.getOrCreate(BedrockComponentCollisionTinting.class).enabled)) {
					particle.collisionTinting = true;
				}
				if(MolangExpression.isOne(emitter.scheme.getOrCreate(BedrockComponentCollisionAppearance.class).enabled)) {
					particle.collisionTexture = true;
				}
				if (this.expireOnImpact)
				{
					if(this.expirationDelay!=0 && particle.expireAge==0)
					{
						particle.expireAge = particle.age+this.expirationDelay;
					}
					else if(this.expirationDelay==0)
					{
						particle.dead = true;

						return;
					}
					
				}

				if (particle.relativePosition)
				{
					particle.relativePosition = false;
					particle.prevPosition.set(prev);
				}

				now.set(aabb.minX + r, aabb.minY + r, aabb.minZ + r);
				
				if (d0 != y)
				{
					try {
						collisionHandler(particle, emitter, 'y', d0, y, now, prev, list2);
					} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
							| IllegalAccessException e) {
						e.printStackTrace();
					}
					now.y += d0 < y ? r : -r;
				}
				
				if (origX != x)
				{
					try {
						collisionHandler(particle, emitter, 'x', origX, x, now, prev, list2);
					} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
							| IllegalAccessException e) {
						e.printStackTrace();
					}
					now.x += origX < x ? r : -r;
				}

				if (origZ != z)
				{
					try {
						collisionHandler(particle, emitter, 'z', origZ, z, now, prev, list2);
					} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
							| IllegalAccessException e) {
						e.printStackTrace();
					}
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
	
	//with java reflect - code redundancy can be avoided quick and easily - may cost a little more performance?
	public void collisionHandler(BedrockParticle particle, BedrockEmitter emitter, char component, double orig, double offset, Vector3d now, Vector3d prev, List<Entity> entities) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		if(!(component=='x' || component=='y' || component=='z')) 
			throw new IllegalArgumentException("Illegal value of the component: "+component);
			
		String componentStr = String.valueOf(component);
		Field vector3fField = Vector3f.class.getField(componentStr);
		
		float collisionTime = vector3fField.getFloat(particle.collisionTime);
		float speed = vector3fField.getFloat(particle.speed);
		float accelerationFactor = vector3fField.getFloat(particle.accelerationFactor);
		
		
		/*realistic collision*/
		if(this.realisticCollision) 
		{
			if(collisionTime!=(particle.age-1)) 
			{
				if(this.bounciness!=0) vector3fField.setFloat(particle.speed, -speed*this.bounciness);
			}
			else if(collisionTime==(particle.age-1)) vector3fField.setFloat(particle.speed, 0);
		}
		else vector3fField.setFloat(particle.accelerationFactor, accelerationFactor*-this.bounciness);
		
		if(collisionTime!=(particle.age-1))
		{
			/*random bounciness*/
			if(this.randomBounciness!=0 /*&& Math.round(particle.speed.x)!=0*/) 
			{
				particle.speed = randomBounciness(particle.speed, component, this.randomBounciness);
			}
			
			/*split particles*/
			if(this.splitParticleCount!=0) 
			{
				splitParticle(particle, emitter, component, orig, offset, now, prev);
			}
			
			/*damping*/
			if(damp!=0) 
			{
				particle.speed = damping(particle.speed);
			}
		}
		if(this.momentum && this.entityCollision) { //NOT FINISHED
			for(Entity entity : entities) 
			{
				particle.speed.x += entity.posX-entity.prevPosX;
				particle.speed.y += entity.posY-entity.prevPosY;
				particle.speed.z += entity.posZ-entity.prevPosZ;
			}
		}
		
		vector3fField.setFloat(particle.collisionTime, particle.age);
	}
	
	public Vector3f damping(Vector3f vector) 
	{
		float randomDamp = (this.randomDamp*0.1f)/2;
		float random = (float) (randomDamp*(Math.random()*2-1));
		float clampedValue = Math.max(0, Math.min(1, (1-damp)+random)); //clamp between 0 and 1
		vector.scale(clampedValue);
		return vector;
	}
	
	public void splitParticle(BedrockParticle particle, BedrockEmitter emitter, char component, double orig, double offset, Vector3d now, Vector3d prev) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if(!(component=='x' || component=='y' || component=='z')) 
			throw new IllegalArgumentException("Illegal value of the component: "+component);

		for(int i = 0; i<this.splitParticleCount;i++) 
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
			splitParticle.collisionTexture = particle.collisionTexture;
			splitParticle.collisionTinting = particle.collisionTinting;
			
			splitParticle.age = particle.age;
			
			String componentStr = String.valueOf(component);
			
			Field vector3fField = Vector3f.class.getField(componentStr);
			Field vector3dField = Vector3d.class.getField(componentStr);
		
			float speed = vector3fField.getFloat(particle.speed);
			double splitPosition = vector3dField.getDouble(splitParticle.position);
			
			if(!(Math.abs(speed)>Math.abs(this.splitParticleSpeedThreshold)))
				return;
			vector3fField.setFloat(splitParticle.collisionTime, particle.age);
			vector3dField.setDouble(splitParticle.position, splitPosition + ((orig < offset) ? this.radius : -this.radius));

			Vector3f randomSpeed = randomBounciness(particle.speed, component, (this.randomBounciness!=0) ? this.randomBounciness : 10);
			randomSpeed.scale(1.0f/this.splitParticleCount);
			splitParticle.speed.set(randomSpeed);
			
			if(damp!=0) 
			{
				splitParticle.speed = damping(splitParticle.speed);
			}
			
			emitter.splitParticles.add(splitParticle);
		}
		particle.dead = true;
	}
	
	public Vector3f randomBounciness(Vector3f vector0, char component, float randomness) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if(randomness!=0 && (component=='x' || component=='y' || component=='z')) 
		{
			
			Vector3f vector = new Vector3f(vector0); //don't change the vector0 - pointer behaviour not wanted here
			float randomfactor = 0.25f; //scale down the vector components not involved in the collision reflection
			float prevLength = vector.length();
			randomness *= 0.1f; //scaled down to 1/10
			float random1 = (float) Math.random()*randomness;
			float random2 = (float) (randomness*randomfactor*(Math.random()*2-1));
			float random3 = (float) (randomness*randomfactor*(Math.random()*2-1));
			
			Field vector3fField = Vector3f.class.getField(String.valueOf(component));
			float vectorValue = vector3fField.getFloat(vector);
			
			switch(component) 
			{
				case 'x':
					vector.y += random2;
					vector.z += random3;
					break;
				case 'y':
					vector.x += random2;
					vector.z += random3;
					break;
				case 'z':
					vector.y += random2;
					vector.x += random3;
					break;
			}
			
			if(bounciness!=0) 
			{
				vector3fField.setFloat(vector, vectorValue + ((vectorValue<0) ? -random1 : random1));
				vector.scale(prevLength/vector.length()); //scale back to original length
			}
			else if(vector.x != 0 || vector.y != 0 || vector.z!=0 )
			{
				/* if bounciness=0 then the speed of a specific component wont't affect the particles movement
				*  so the particles speed needs to be scaled back without taking that component into account
				*  when bounciness=0 the energy of that component gets absorbed by the collision block and therefore is lost for the particle 
				*/
				if(MolangExpression.isOne(this.preserveEnergy)) vector3fField.setFloat(vector, 0); 
				//if the vector is now zero... don't execute 1/vector.length() -> 1/0 not possible
				if(vector.x != 0 || vector.y != 0 || vector.z!=0) vector.scale(prevLength/vector.length());
				vector3fField.setFloat(vector, vectorValue);
			}
			else //bounciness==0 and vector is zero (rare case, but not impossible)
			{
				//if you don't want particles to stop, while others randomly slide away, 
				//when bounciness==0, then return vector0
				return vector0; 
			}
			
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
