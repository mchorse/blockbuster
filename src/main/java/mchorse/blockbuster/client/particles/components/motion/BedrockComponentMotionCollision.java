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
import mchorse.mclib.math.Operation;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.util.HashMap;
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
    public float damp = 0; // should be like in Blender
    public int splitParticleCount;
    public float splitParticleSpeedThreshold; // threshold to activate the split
    public float radius = 0.01F;
    public boolean expireOnImpact;
    public MolangExpression expirationDelay = MolangParser.ZERO;
    public boolean realisticCollision;

    /* Runtime options */
    public boolean json;
    private Vector3d previous = new Vector3d();
    private Vector3d current = new Vector3d();
    private BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    public static float getComponent(Vector3f vector, EnumFacing.Axis component)
    {
        if (component == EnumFacing.Axis.X)
        {
            return vector.x;
        }
        else if (component == EnumFacing.Axis.Y)
        {
            return vector.y;
        }

        return vector.z;
    }

    public static void setComponent(Vector3f vector, EnumFacing.Axis component, float value)
    {
        if (component == EnumFacing.Axis.X)
        {
            vector.x = value;
        }
        else if (component == EnumFacing.Axis.Y)
        {
            vector.y = value;
        }
        else
        {
            vector.z = value;
        }
    }

    public static double getComponent(Vector3d vector, EnumFacing.Axis component)
    {
        if (component == EnumFacing.Axis.X)
        {
            return vector.x;
        }
        else if (component == EnumFacing.Axis.Y)
        {
            return vector.y;
        }

        return vector.z;
    }

    public static void setComponent(Vector3d vector, EnumFacing.Axis component, double value)
    {
        if (component == EnumFacing.Axis.X)
        {
            vector.x = value;
        }
        else if (component == EnumFacing.Axis.Y)
        {
            vector.y = value;
        }
        else
        {
            vector.z = value;
        }
    }

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
        if (element.has("expirationDelay")) this.expirationDelay = parser.parseJson(element.get("expirationDelay"));
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
        if (!MolangExpression.isZero(this.expirationDelay)) object.add("expirationDelay", this.expirationDelay.toJson());

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

            List<Entity> entities = emitter.world.getEntitiesWithinAABB(Entity.class, aabb.expand(x, y, z));
            HashMap<Entity, AxisAlignedBB> entityAABBs = new HashMap<Entity, AxisAlignedBB>();
            /* for own hitbox implementation: check for hitbox expanded for the previous position - prevent fast moving tunneling */
            List<AxisAlignedBB> list = emitter.world.getCollisionBoxes(null, aabb.expand(x, y, z));

            if (this.entityCollision)
            {
                for (Entity entity : entities)
                {
                    AxisAlignedBB aabb2 = aabb;
                    AxisAlignedBB entityAABB = entity.getEntityBoundingBox();


                    double y2 = y, x2 = x, z2 = z;

                    y2 = entityAABB.calculateYOffset(aabb2, y2);
                    aabb2 = aabb2.offset(0.0D, y2, 0.0D);

                    x2 = entityAABB.calculateXOffset(aabb2, x2);
                    aabb2 = aabb2.offset(x2, 0.0D, 0.0D);

                    z2 = entityAABB.calculateZOffset(aabb2, z2);
                    aabb2 = aabb2.offset(0.0D, 0.0D, z2);

                    Vector3f speedEntity = new Vector3f((float) (entity.posX - entity.prevPosX), (float) (entity.posY - entity.prevPosY), (float) (entity.posZ - entity.prevPosZ));

                    if (this.momentum && this.entityCollision)
                    {
                        particle.speed.x += 2 * speedEntity.x;
                        particle.speed.y += 2 * speedEntity.y;
                        particle.speed.z += 2 * speedEntity.z;
                    }

                    if (d0 == y2 && origX == x2 && origZ == z2) entityAABBs.put(entity, entityAABB);
                    else list.add(entityAABB);
                }
            }

            Object[] offsetData = calculateOffsets(aabb, list, x, y, z);
            aabb = (AxisAlignedBB) offsetData[0];
            x = (double) offsetData[1];
            y = (double) offsetData[2];
            z = (double) offsetData[3];

            if (d0 != y || origX != x || origZ != z)
            {
                collision(particle, emitter, prev);

                now.set(aabb.minX + r, aabb.minY + r, aabb.minZ + r);

                if (d0 != y)
                {
                    if (d0 < y) now.y = aabb.minY;
                    else now.y = aabb.maxY;
                    now.y += d0 < y ? r : -r;
                    collisionHandler(particle, emitter, EnumFacing.Axis.Y, now, prev);
                }

                if (origX != x)
                {
                    if (origX < x) now.x = aabb.minX;
                    else now.x = aabb.maxX;
                    now.x += origX < x ? r : -r;

                    collisionHandler(particle, emitter, EnumFacing.Axis.X, now, prev);
                }

                if (origZ != z)
                {
                    if (origZ < z) now.z = aabb.minZ;
                    else now.z = aabb.maxZ;
                    now.z += origZ < z ? r : -r;

                    collisionHandler(particle, emitter, EnumFacing.Axis.Z, now, prev);
                }

                particle.position.set(now);

				/* only apply drag when speed is almost not zero and randombounciness and realisticCollision are off
				 * prevent particles from accelerating away when randomBounciness is active */
                if (!((this.randomBounciness != 0 || this.realisticCollision) && Math.round(particle.speed.length()) == 0))
                {
                    particle.dragFactor += this.collissionDrag;
                }
            }

            for (HashMap.Entry<Entity, AxisAlignedBB> entry : entityAABBs.entrySet())
            {

                AxisAlignedBB entityAABB = entry.getValue();
                Entity entity = entry.getKey();

                Vector3f speedEntity = new Vector3f((float) (entity.posX - entity.prevPosX), (float) (entity.posY - entity.prevPosY), (float) (entity.posZ - entity.prevPosZ));
                Vector3f ray;

                if (speedEntity.x != 0 || speedEntity.y != 0 || speedEntity.z != 0)
                {
                    ray = speedEntity;
                }
                else
                {
                    continue;
                }

                Vector3d frac = intersect(ray, particle.getGlobalPosition(emitter), entityAABB);
                if (frac != null)
                {
                    particle.position.add(frac);

                    AxisAlignedBB aabb2 = new AxisAlignedBB(particle.position.x - r, particle.position.y - r, particle.position.z - r, particle.position.x + r, particle.position.y + r, particle.position.z + r);

                    collision(particle, emitter, prev);

                    if ((aabb2.minX < entityAABB.maxX && aabb2.maxX > entityAABB.maxX) || (aabb2.maxX > entityAABB.minX && aabb2.minX < entityAABB.minX))
                    {
                        /* collisionTime should be not changed - otherwise the particles will stop when moving against moving entites */
                        float tmpTime = particle.collisionTime.x;
                        double delta = particle.position.x - entity.posX;
                        particle.position.x += delta > 0 ? r : -r;
                        collisionHandler(particle, emitter, EnumFacing.Axis.X, particle.position, prev);

                        /* collisionTime should not change or otherwise particles will lose their speed although they should be reflected */
                        particle.collisionTime.x = tmpTime;

                        /* particle speed is always switched (realistcCollision==true), as it always collides with the entity, but it should only have one correct direction */
                        if (particle.speed.x > 0)
                        {
                            if (speedEntity.x < 0) particle.speed.x = -particle.speed.x;
                        }
                        else if (particle.speed.x < 0)
                        {
                            if (speedEntity.x > 0) particle.speed.x = -particle.speed.x;
                        }

                        /* otherwise particles would stick on the body and get reflected when entity stops */
                        particle.position.x += particle.speed.x / 20F;
                    }

                    if ((aabb2.minY < entityAABB.maxY && aabb2.maxY > entityAABB.maxY) || (aabb2.maxY > entityAABB.minY && aabb2.minY < entityAABB.minY))
                    {
                        float tmpTime = particle.collisionTime.y;
                        double delta = particle.position.y - entity.posY;
                        particle.position.y += delta > 0 ? r : -r;
                        collisionHandler(particle, emitter, EnumFacing.Axis.Y, particle.position, prev);

                        particle.collisionTime.y = tmpTime;

                        if (particle.speed.y > 0)
                        {
                            if (speedEntity.y < 0) particle.speed.y = -particle.speed.y;
                        }
                        else if (particle.speed.y < 0)
                        {
                            if (speedEntity.y > 0) particle.speed.y = -particle.speed.y;
                        }

                        particle.position.y += particle.speed.y / 20F;
                    }

                    if ((aabb2.minZ < entityAABB.maxZ && aabb2.maxZ > entityAABB.maxZ) || (aabb2.maxZ > entityAABB.minZ && aabb2.minZ < entityAABB.minZ))
                    {
                        float tmpTime = particle.collisionTime.z;
                        double delta = particle.position.z - entity.posZ;
                        particle.position.z += delta > 0 ? r : -r;
                        collisionHandler(particle, emitter, EnumFacing.Axis.Z, particle.position, prev);

                        particle.collisionTime.z = tmpTime;

                        if (particle.speed.z > 0)
                        {
                            if (speedEntity.z < 0) particle.speed.z = -particle.speed.z;
                        }
                        else if (particle.speed.z < 0)
                        {
                            if (speedEntity.z > 0) particle.speed.z = -particle.speed.z;
                        }

                        particle.position.z += particle.speed.z / 20F;
                    }
                }
            }
        }
    }

    public void collision(BedrockParticle particle, BedrockEmitter emitter, Vector3d prev)
    {
        if (MolangExpression.isOne(emitter.scheme.getOrCreate(BedrockComponentCollisionTinting.class).enabled))
        {
            particle.collisionTinting = true;
            particle.firstCollision = particle.age;
        }

        if (MolangExpression.isOne(emitter.scheme.getOrCreate(BedrockComponentCollisionAppearance.class).enabled))
        {
            particle.collisionTexture = true;
            particle.firstCollision = particle.age;
        }

        if (this.expireOnImpact)
        {
            if (this.expirationDelay.get() != 0 && particle.expireAge == 0)
            {
                particle.expireAge = (int) (particle.age + Math.abs(this.expirationDelay.get()));
                particle.expirationDelay = (int) this.expirationDelay.get();
            }
            else if (this.expirationDelay.get() == 0)
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
    }

    public void collisionHandler(BedrockParticle particle, BedrockEmitter emitter, EnumFacing.Axis component, Vector3d now, Vector3d prev)
    {
        float collisionTime = getComponent(particle.collisionTime, component);
        float speed = getComponent(particle.speed, component);
        float accelerationFactor = getComponent(particle.accelerationFactor, component);

        /* realistic collision */
        if (this.realisticCollision)
        {
            if (collisionTime != (particle.age - 1))
            {
                if (this.bounciness != 0)
                {
                    setComponent(particle.speed, component, -speed * this.bounciness);
                }
            }
            else if (collisionTime == (particle.age - 1))
            {
                setComponent(particle.speed, component, 0); //particle laid on that surface since last tick
            }
        }
        else
        {
            setComponent(particle.accelerationFactor, component, accelerationFactor * -this.bounciness);
        }

        if (collisionTime != (particle.age - 1))
        {
            /* random bounciness */
            if (this.randomBounciness != 0 /* && Math.round(particle.speed.x) != 0 */)
            {
                particle.speed = randomBounciness(particle.speed, component, this.randomBounciness);
            }

            /* split particles */
            if (this.splitParticleCount != 0)
            {
                splitParticle(particle, emitter, component, now, prev);
            }

            /* damping */
            if (damp != 0)
            {
                particle.speed = damping(particle.speed);
            }
        }

        setComponent(particle.collisionTime, component, particle.age);
    }

    public Vector3f damping(Vector3f vector)
    {
        float random = (float) (this.randomDamp * (Math.random() * 2 - 1));
        float clampedValue = MathUtils.clamp((1 - this.damp) + random, 0, 1);

        vector.scale(clampedValue);

        return vector;
    }

    public void splitParticle(BedrockParticle particle, BedrockEmitter emitter, EnumFacing.Axis component, Vector3d now, Vector3d prev)
    {
        for (int i = 0; i < this.splitParticleCount; i++)
        {
            BedrockParticle splitParticle = emitter.createParticle(false);
            splitParticle.initialPosition.set(particle.initialPosition);
            splitParticle.collisionTime.set(particle.collisionTime);
            splitParticle.position.set(now);
            splitParticle.prevPosition.set(prev);

            /* Mh is this necessary? */
            splitParticle.acceleration.set(particle.acceleration);
            splitParticle.accelerationFactor.set(particle.accelerationFactor);
            splitParticle.drag = particle.drag;
            splitParticle.dragFactor = particle.dragFactor;
            splitParticle.collisionTexture = particle.collisionTexture;
            splitParticle.collisionTinting = particle.collisionTinting;
            splitParticle.expirationDelay = particle.expirationDelay;
            splitParticle.expireAge = particle.expireAge;
            splitParticle.firstCollision = particle.firstCollision;

            splitParticle.age = particle.age;

            float speed = getComponent(particle.speed, component);
            double splitPosition = getComponent(splitParticle.position, component);

            if (!(Math.abs(speed) > Math.abs(this.splitParticleSpeedThreshold)))
            {
                return;
            }

            setComponent(splitParticle.collisionTime, component, particle.age);
            setComponent(splitParticle.position, component, splitPosition/* + ((orig < offset) ? this.radius : -this.radius)*/);

            Vector3f randomSpeed = randomBounciness(particle.speed, component, (this.randomBounciness != 0) ? this.randomBounciness : 10);
            randomSpeed.scale(1.0f / this.splitParticleCount);
            splitParticle.speed.set(randomSpeed);

            if (this.damp != 0)
            {
                splitParticle.speed = damping(splitParticle.speed);
            }

            emitter.splitParticles.add(splitParticle);
        }

        particle.dead = true;
    }

    public Vector3f randomBounciness(Vector3f vector0, EnumFacing.Axis component, float randomness)
    {
        if (randomness != 0)
        {
            /* don't change the vector0 - pointer behaviour not wanted here */
            Vector3f vector = new Vector3f(vector0);
            /* scale down the vector components not involved in the collision reflection */
            float randomfactor = 0.25F;
            float prevLength = vector.length();
            randomness *= 0.1F;
            float random1 = (float) Math.random() * randomness;
            float random2 = (float) (randomness * randomfactor * (Math.random() * 2 - 1));
            float random3 = (float) (randomness * randomfactor * (Math.random() * 2 - 1));

            float vectorValue = getComponent(vector, component);

            if (component == EnumFacing.Axis.X)
            {
                vector.y += random2;
                vector.z += random3;
            }
            else if (component == EnumFacing.Axis.Y)
            {
                vector.x += random2;
                vector.z += random3;
            }
            else
            {
                vector.y += random2;
                vector.x += random3;
            }

            if (this.bounciness != 0)
            {
                setComponent(vector, component, vectorValue + ((vectorValue < 0) ? -random1 : random1));
                vector.scale(prevLength / vector.length()); //scale back to original length
            }
            else if (vector.x != 0 || vector.y != 0 || vector.z != 0)
            {
                /* if bounciness=0 then the speed of a specific component wont't affect the particles movement
                 * so the particles speed needs to be scaled back without taking that component into account
                 * when bounciness=0 the energy of that component gets absorbed by the collision block and therefore is lost for the particle
                 */
                if (MolangExpression.isOne(this.preserveEnergy))
                {
                    setComponent(vector, component, 0);
                }

                /* if the vector is now zero... don't execute 1/vector.length() -> 1/0 not possible */
                if (vector.x != 0 || vector.y != 0 || vector.z != 0)
                {
                    vector.scale(prevLength / vector.length());
                }

                setComponent(vector, component, vectorValue);
            }
            else /* bounciness == 0 and vector is zero (rare case, but not impossible) */
            {
                /* if you don't want particles to stop, while others randomly slide away,
                 * when bounciness==0, then return vector0 */
                return vector0;
            }

            return vector;
        }

        return vector0;
    }

    public Vector3d intersect(Vector3f ray, Vector3d orig, AxisAlignedBB aabb)
    {
        double tmin = (aabb.minX - orig.x) / ray.x;
        double tmax = (aabb.maxX - orig.x) / ray.x;

        if (tmin > tmax)
        {
            double tminTmp = tmin;
            tmin = tmax;
            tmax = tminTmp;
        }

        double tymin = (aabb.minY - orig.y) / ray.y;
        double tymax = (aabb.maxY - orig.y) / ray.y;

        if (tymin > tymax)
        {
            double tyminTmp = tymin;
            tymin = tymax;
            tymax = tyminTmp;
        }

        if (tmin > tymax || tymin > tmax)
            return null;

        if (tymin > tmin)
            tmin = tymin;

        if (tymax < tmax)
            tmax = tymax;

        double tzmin = (aabb.minZ - orig.z) / ray.z;
        double tzmax = (aabb.maxZ - orig.z) / ray.z;

        if (tzmin > tzmax)
        {
            double tzminTmp = tzmin;
            tzmin = tzmax;
            tzmax = tzminTmp;
        }

        if (tmin > tzmax || tzmin > tmax)
            return null;

        if (tzmax < tmax)
            tmax = tzmax;

        Vector3d ray1 = new Vector3d(ray);

        ray1.scale(tmax);

        return ray1;
    }

    /*
     * @param aabb AxisAlignedBoundingBox of the main aabb
     * @param list List of AxisAlignedBoundingBoxs of the targets
     * @param xyz origins
     * @return Object Array {aabb, x, y, z}
     */
    public Object[] calculateOffsets(AxisAlignedBB aabb, List<AxisAlignedBB> list, double x, double y, double z)
    {
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

        return new Object[] {aabb, x, y, z};
    }

    @Override
    public int getSortingIndex()
    {
        return 50;
    }
}
