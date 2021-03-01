package mchorse.blockbuster.client.particles.emitter;

import net.minecraft.entity.Entity;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.util.HashMap;

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
    /* Age when the particle should expire */
    public int expireAge;
    /* Used to determine lifetime when expirationDelay is on */
    public int expirationDelay;
    public boolean dead;
    public boolean relativePosition;
    public boolean relativeRotation;
    public boolean relativeDirection;
    public boolean relativeAcceleration;
    public boolean realisticCollisionDrag;
    /* Works best with relativeDirection */
    public boolean gravity;
    public boolean manual;
    
    /**
     * This is used to estimate whether an object is only bouncing or lying on a surface
     *
     * CollisionTime won't work when e.g. the particle bounces of the surface and directly in the next
     * update cycle hits the same surface side, like from top of the block to bottom of the block...
     * I think this probably never happens in practice
     */
    public Vector3f collisionTime = new Vector3f(-2f, -2f,-2f);
    public HashMap<Entity, Vector3f> entityCollisionTime = new HashMap<>();
    public boolean collisionTexture;
    public boolean collisionTinting;
    public int bounces;

    /**
     * For collision Appearance needed for animation
     */
    public int firstCollision = -1;
    
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
            if(this.realisticCollisionDrag && Math.round(this.speed.x*10000) == 0 && Math.round(this.speed.y*10000) == 0 && Math.round(this.speed.z*10000) == 0)
            {
                this.dragFactor = 0;
                this.speed.scale(0);
            }

            float rotationAcceleration = this.rotationAcceleration / 20F -this.rotationDrag * this.rotationVelocity;
            this.rotationVelocity += rotationAcceleration / 20F;
            this.rotation = this.initialRotation + this.rotationVelocity * this.age;

            /* Position */
            if (this.relativeDirection && this.age == 0)
            {
                emitter.rotation.transform(this.speed);
            }

            if (this.relativeAcceleration)
            {
                emitter.rotation.transform(this.acceleration);
            }
            
            Vector3f drag = new Vector3f(this.speed);

            drag.scale(-(this.drag + this.dragFactor));

            if (this.gravity)
            {
                this.acceleration.y -= 9.81;
            }

            this.acceleration.add(drag);
            this.acceleration.scale(1 / 20F);
            this.speed.add(this.acceleration);

            Vector3f speed0 = new Vector3f(this.speed);
            speed0.x *= this.accelerationFactor.x;
            speed0.y *= this.accelerationFactor.y;
            speed0.z *= this.accelerationFactor.z;

            if (this.relativePosition || this.relativeRotation)
            {
                this.matrix.transform(speed0);
            }
            
            this.position.x += speed0.x / 20F;
            this.position.y += speed0.y / 20F;
            this.position.z += speed0.z / 20F;
        }

        if (this.lifetime >= 0 &&
            (this.age >= this.lifetime || (this.age>=this.expireAge && this.expireAge!=0)) )
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
