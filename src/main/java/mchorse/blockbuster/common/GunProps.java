package mchorse.blockbuster.common;

import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.entity.EntityGunProjectile;
import mchorse.metamorph.api.Morph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Blockbuster gun properties
 * 
 * This savage fellow is responsible for keeping all of the properties 
 * that a gun can have. Those are including: gun properties itself, 
 * projectile that will be spawned from the gun, and projectile impact 
 * properties.
 */
public class GunProps
{
    /* Gun properties */
    public AbstractMorph defaultMorph;
    public AbstractMorph firingMorph;
    public String fireCommand;
    public int delay;
    public int projectiles;
    public float scatter;
    public boolean launch;
    public boolean useTarget;

    /* Projectile properties */
    public AbstractMorph projectileMorph;
    public String tickCommand;
    public int ticking;
    public int lifeSpan;
    public boolean yaw;
    public boolean pitch;
    public boolean sequencer;
    public boolean random;
    public float hitboxX;
    public float hitboxY;
    public float speed;
    public float friction;
    public float gravity;
    public int fadeIn;
    public int fadeOut;

    /* Impact properties */
    public AbstractMorph impactMorph;
    public String impactCommand;
    public int impactDelay;
    public boolean vanish;
    public boolean bounce;
    public boolean sticks;
    public int hits;
    public float damage;
    public float bounceFactor;
    public int vanishDelay;
    public float penetration;

    /* Transforms */
    public ModelTransform gunTransform = new ModelTransform();
    public ModelTransform projectileTransform = new ModelTransform();

    private int shoot = 0;
    private Morph current = new Morph();
    private boolean renderLock;

    public EntityLivingBase entity;

    public GunProps()
    {
        this.reset();
    }

    public GunProps(NBTTagCompound tag)
    {
        this.fromNBT(tag);
    }

    public void shot()
    {
        if (this.delay <= 0)
        {
            return;
        }

        this.shoot = this.delay;
        this.current.set(MorphUtils.copy(this.firingMorph));
    }

    @SideOnly(Side.CLIENT)
    public void createEntity()
    {
        this.createEntity(Minecraft.getMinecraft().world);
    }

    public void createEntity(World world)
    {
        if (this.entity != null)
        {
            return;
        }

        this.entity = new EntityActor(world);
        this.entity.onGround = true;
        this.entity.rotationYaw = this.entity.prevRotationYaw = 0;
        this.entity.rotationYawHead = this.entity.prevRotationYawHead = 0;
        this.entity.rotationPitch = this.entity.prevRotationPitch = 0;
    }

    public EntityLivingBase getEntity(EntityGunProjectile entity)
    {
        if (this.entity != null)
        {
            this.entity.prevPosX = entity.prevPosX;
            this.entity.prevPosY = entity.prevPosY;
            this.entity.prevPosZ = entity.prevPosZ;

            this.entity.posX = entity.posX;
            this.entity.posY = entity.posY;
            this.entity.posZ = entity.posZ;
        }

        return this.entity;
    }

    @SideOnly(Side.CLIENT)
    public void update()
    {
        if (this.entity != null)
        {
            this.entity.ticksExisted++;

            AbstractMorph morph = this.current.get();

            if (morph != null)
            {
                morph.update(this.entity);
            }
        }

        if (this.shoot >= 0)
        {
            if (this.shoot == 0)
            {
                this.current.set(MorphUtils.copy(this.defaultMorph));
            }

            this.shoot--;
        }
    }

    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase target, float partialTicks)
    {
        if (this.renderLock)
        {
            return;
        }

        this.renderLock = true;

        if (this.entity == null)
        {
            this.createEntity();
        }

        EntityLivingBase entity = this.useTarget ? target : this.entity;
        AbstractMorph morph = this.current.get();

        if (morph != null && entity != null)
        {
            float rotationYaw = entity.renderYawOffset;
            float prevRotationYaw = entity.prevRenderYawOffset;
            float rotationYawHead = entity.rotationYawHead;
            float prevRotationYawHead = entity.prevRotationYawHead;

            entity.rotationYawHead -= entity.renderYawOffset;
            entity.prevRotationYawHead -= entity.prevRenderYawOffset;
            entity.renderYawOffset = entity.prevRenderYawOffset = 0.0F;

            GL11.glPushMatrix();
            GL11.glTranslatef(0.5F, 0, 0.5F);
            this.gunTransform.transform();

            this.setupEntity();
            MorphUtils.render(morph, entity, 0, 0, 0, 0, partialTicks);

            GL11.glPopMatrix();

            entity.renderYawOffset = rotationYaw;
            entity.prevRenderYawOffset = prevRotationYaw;
            entity.rotationYawHead = rotationYawHead;
            entity.prevRotationYawHead = prevRotationYawHead;
        }

        this.renderLock = false;
    }

    @SideOnly(Side.CLIENT)
    private void setupEntity()
    {
        /* Reset entity's values, just in case some weird shit is going 
         * to happen in morph's update code*/
        this.entity.setPositionAndRotation(0, 0, 0, 0, 0);
        this.entity.setLocationAndAngles(0, 0, 0, 0, 0);
        this.entity.rotationYawHead = this.entity.prevRotationYawHead = 0;
        this.entity.rotationYaw = this.entity.prevRotationYaw = 0;
        this.entity.rotationPitch = this.entity.prevRotationPitch = 0;
        this.entity.renderYawOffset = this.entity.prevRenderYawOffset = 0;
        this.entity.setVelocity(0, 0, 0);
    }

    /**
     * Reset properties to default values 
     */
    public void reset()
    {
        /* Gun properties */
        this.defaultMorph = null;
        this.firingMorph = null;
        this.fireCommand = "";
        this.delay = 0;
        this.projectiles = 1;
        this.scatter = 0F;
        this.launch = false;
        this.useTarget = false;

        /* Projectile properties */
        this.projectileMorph = null;
        this.tickCommand = "";
        this.ticking = 0;
        this.lifeSpan = 200;
        this.yaw = true;
        this.pitch = true;
        this.sequencer = false;
        this.random = false;
        this.hitboxX = 0.25F;
        this.hitboxY = 0.25F;
        this.speed = 1.0F;
        this.friction = 0.99F;
        this.gravity = 0.03F;
        this.fadeIn = this.fadeOut = 10;

        /* Impact properties */
        this.impactMorph = null;
        this.impactCommand = "";
        this.impactDelay = 0;
        this.vanish = true;
        this.bounce = false;
        this.sticks = false;
        this.hits = 1;
        this.damage = 0F;
        this.bounceFactor = 1F;
        this.vanishDelay = 0;
        this.penetration = 0;

        /* Transforms */
        this.gunTransform = new ModelTransform();
        this.projectileTransform = new ModelTransform();
    }

    public void fromNBT(NBTTagCompound tag)
    {
        this.reset();

        /* Gun properties */
        this.defaultMorph = this.create(tag, "Morph");
        this.firingMorph = this.create(tag, "Fire");
        if (tag.hasKey("FireCommand")) this.fireCommand = tag.getString("FireCommand");
        if (tag.hasKey("Delay")) this.delay = tag.getInteger("Delay");
        if (tag.hasKey("Projectiles")) this.projectiles = tag.getInteger("Projectiles");
        if (tag.hasKey("Scatter")) this.scatter = tag.getFloat("Scatter");
        if (tag.hasKey("Launch")) this.launch = tag.getBoolean("Launch");
        if (tag.hasKey("Target")) this.useTarget = tag.getBoolean("Target");

        /* Projectile properties */
        this.projectileMorph = this.create(tag, "Projectile");
        if (tag.hasKey("TickCommand")) this.tickCommand = tag.getString("TickCommand");
        if (tag.hasKey("Ticking")) this.ticking = tag.getInteger("Ticking");
        if (tag.hasKey("LifeSpan")) this.lifeSpan = tag.getInteger("LifeSpan");
        if (tag.hasKey("Yaw")) this.yaw = tag.getBoolean("Yaw");
        if (tag.hasKey("Pitch")) this.pitch = tag.getBoolean("Pitch");
        if (tag.hasKey("Sequencer")) this.sequencer = tag.getBoolean("Sequencer");
        if (tag.hasKey("Random")) this.random = tag.getBoolean("Random");
        if (tag.hasKey("HX")) this.hitboxX = tag.getFloat("HX");
        if (tag.hasKey("HY")) this.hitboxY = tag.getFloat("HY");
        if (tag.hasKey("Speed")) this.speed = tag.getFloat("Speed");
        if (tag.hasKey("Friction")) this.friction = tag.getFloat("Friction");
        if (tag.hasKey("Gravity")) this.gravity = tag.getFloat("Gravity");
        if (tag.hasKey("FadeIn")) this.fadeIn = tag.getInteger("FadeIn");
        if (tag.hasKey("FadeOut")) this.fadeOut = tag.getInteger("FadeOut");

        /* Impact properties */
        this.impactMorph = this.create(tag, "Impact");
        if (tag.hasKey("ImpactCommand")) this.impactCommand = tag.getString("ImpactCommand");
        if (tag.hasKey("ImpactDelay")) this.impactDelay = tag.getInteger("ImpactDelay");
        if (tag.hasKey("Vanish")) this.vanish = tag.getBoolean("Vanish");
        if (tag.hasKey("Bounce")) this.bounce = tag.getBoolean("Bounce");
        if (tag.hasKey("Stick")) this.sticks = tag.getBoolean("Stick");
        if (tag.hasKey("Hits")) this.hits = tag.getInteger("Hits");
        if (tag.hasKey("Damage")) this.damage = tag.getFloat("Damage");
        if (tag.hasKey("BFactor")) this.bounceFactor = tag.getFloat("BFactor");
        if (tag.hasKey("VDelay")) this.vanishDelay = tag.getInteger("VDelay");
        if (tag.hasKey("Penetration")) this.penetration = tag.getFloat("Penetration");

        /* Transforms */
        if (tag.hasKey("Gun")) this.gunTransform.fromNBT(tag.getCompoundTag("Gun"));
        if (tag.hasKey("Transform")) this.projectileTransform.fromNBT(tag.getCompoundTag("Transform"));

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
        {
            this.current.set(MorphUtils.copy(this.defaultMorph));
        }
    }

    private AbstractMorph create(NBTTagCompound tag, String key)
    {
        if (tag.hasKey(key, NBT.TAG_COMPOUND))
        {
            return MorphManager.INSTANCE.morphFromNBT(tag.getCompoundTag(key));
        }

        return null;
    }

    public NBTTagCompound toNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        /* Gun properties */
        if (this.defaultMorph != null) tag.setTag("Morph", this.to(this.defaultMorph));
        if (this.firingMorph != null) tag.setTag("Fire", this.to(this.firingMorph));
        if (!this.fireCommand.isEmpty()) tag.setString("FireCommand", this.fireCommand);
        if (this.delay != 0) tag.setInteger("Delay", this.delay);
        if (this.projectiles != 1) tag.setInteger("Projectiles", this.projectiles);
        if (this.scatter != 0F) tag.setFloat("Scatter", this.scatter);
        if (this.launch) tag.setBoolean("Launch", this.launch);
        if (this.useTarget) tag.setBoolean("Target", this.useTarget);

        /* Projectile properties */
        if (this.projectileMorph != null) tag.setTag("Projectile", this.to(this.projectileMorph));
        if (!this.tickCommand.isEmpty()) tag.setString("TickCommand", this.tickCommand);
        if (this.ticking != 0) tag.setInteger("Ticking", this.ticking);
        if (this.lifeSpan != 200) tag.setInteger("LifeSpan", this.lifeSpan);
        if (!this.yaw) tag.setBoolean("Yaw", this.yaw);
        if (!this.pitch) tag.setBoolean("Pitch", this.pitch);
        if (this.sequencer) tag.setBoolean("Sequencer", this.sequencer);
        if (this.random) tag.setBoolean("Random", this.random);
        if (this.hitboxX != 0.25F) tag.setFloat("HX", this.hitboxX);
        if (this.hitboxY != 0.25F) tag.setFloat("HY", this.hitboxY);
        if (this.speed != 1.0F) tag.setFloat("Speed", this.speed);
        if (this.friction != 0.99F) tag.setFloat("Friction", this.friction);
        if (this.gravity != 0.03F) tag.setFloat("Gravity", this.gravity);
        if (this.fadeIn != 10) tag.setInteger("FadeIn", this.fadeIn);
        if (this.fadeOut != 10) tag.setInteger("FadeOut", this.fadeOut);

        /* Impact properties */
        if (this.impactMorph != null) tag.setTag("Impact", this.to(this.impactMorph));
        if (!this.impactCommand.isEmpty()) tag.setString("ImpactCommand", this.impactCommand);
        if (this.impactDelay != 0) tag.setInteger("ImpactDelay", this.impactDelay);
        if (!this.vanish) tag.setBoolean("Vanish", this.vanish);
        if (this.bounce) tag.setBoolean("Bounce", this.bounce);
        if (this.sticks) tag.setBoolean("Stick", this.sticks);
        if (this.hits != 1) tag.setInteger("Hits", this.hits);
        if (this.damage != 0) tag.setFloat("Damage", this.damage);
        if (this.bounceFactor != 1F) tag.setFloat("BFactor", this.bounceFactor);
        if (this.vanishDelay != 0) tag.setInteger("VDelay", this.vanishDelay);
        if (this.penetration != 0) tag.setFloat("Penetration", this.penetration);

        /* Transforms */
        if (!this.gunTransform.isDefault()) tag.setTag("Gun", this.gunTransform.toNBT());
        if (!this.projectileTransform.isDefault()) tag.setTag("Transform", this.projectileTransform.toNBT());

        return tag;
    }

    private NBTTagCompound to(AbstractMorph morph)
    {
        NBTTagCompound tag = new NBTTagCompound();
        morph.toNBT(tag);

        return tag;
    }
}