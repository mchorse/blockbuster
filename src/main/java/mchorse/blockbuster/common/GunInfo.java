package mchorse.blockbuster.common;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * BB gun properties 
 */
public class GunInfo
{
    public AbstractMorph defaultMorph;
    public AbstractMorph firingMorph;
    public AbstractMorph projectileMorph;

    public int delay;
    public int fireRate;
    public boolean auto;
    public float accuracy;
    public int projectiles;
    public String fireCommand;
    public String tickCommand;
    public String impactCommand;
    public int ticking;
    public int lifeSpan;
    public float speed;
    public float friction;
    public float gravity;
    public boolean vanish;
    public float damage;

    @SideOnly(Side.CLIENT)
    private EntityLivingBase entity;

    public GunInfo()
    {
        this.reset();
    }

    public GunInfo(NBTTagCompound tag)
    {
        this.fromNBT(tag);
    }

    @SideOnly(Side.CLIENT)
    public void createEntity()
    {
        if (this.entity != null)
        {
            return;
        }

        this.entity = new EntityActor(Minecraft.getMinecraft().theWorld);
        this.entity.onGround = true;
    }

    public void reset()
    {
        this.defaultMorph = this.firingMorph = this.projectileMorph = null;

        this.delay = 0;
        this.fireRate = 5;
        this.auto = false;
        this.accuracy = 0F;
        this.projectiles = 1;
        this.fireCommand = this.tickCommand = this.impactCommand = "";
        this.ticking = 0;
        this.lifeSpan = 200;
        this.speed = 0.1F;
        this.friction = 0.95F;
        this.gravity = 0.01F;
        this.vanish = true;
        this.damage = 0F;
    }

    public void fromNBT(NBTTagCompound tag)
    {
        this.reset();

        this.defaultMorph = this.create(tag, "Morph");
        this.firingMorph = this.create(tag, "Fire");
        this.projectileMorph = this.create(tag, "Projectile");

        if (tag.hasKey("Delay")) this.delay = tag.getInteger("Delay");
        if (tag.hasKey("FireRate")) this.fireRate = tag.getInteger("FireRate");
        if (tag.hasKey("Auto")) this.auto = tag.getBoolean("Auto");
        if (tag.hasKey("Accuracy")) this.accuracy = tag.getFloat("Accuracy");
        if (tag.hasKey("Projectiles")) this.projectiles = tag.getInteger("Projectiles");
        if (tag.hasKey("FireCommand")) this.fireCommand = tag.getString("FireCommand");
        if (tag.hasKey("TickCommand")) this.tickCommand = tag.getString("TickCommand");
        if (tag.hasKey("ImpactCommand")) this.impactCommand = tag.getString("ImpactCommand");
        if (tag.hasKey("Ticking")) this.ticking = tag.getInteger("Ticking");
        if (tag.hasKey("LifeSpan")) this.lifeSpan = tag.getInteger("LifeSpan");
        if (tag.hasKey("Speed")) this.speed = tag.getFloat("Speed");
        if (tag.hasKey("Friction")) this.friction = tag.getFloat("Friction");
        if (tag.hasKey("Gravity")) this.gravity = tag.getFloat("Gravity");
        if (tag.hasKey("Vanish")) this.vanish = tag.getBoolean("Vanish");
        if (tag.hasKey("Damage")) this.damage = tag.getFloat("Damage");
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

        if (this.defaultMorph != null) tag.setTag("Morph", this.to(this.defaultMorph));
        if (this.firingMorph != null) tag.setTag("Fire", this.to(this.firingMorph));
        if (this.projectileMorph != null) tag.setTag("Projectile", this.to(this.projectileMorph));

        if (this.delay != 0) tag.setInteger("Delay", this.delay);
        if (this.fireRate != 5) tag.setInteger("FireRate", this.fireRate);
        if (this.auto) tag.setBoolean("Auto", this.auto);
        if (this.accuracy != 0F) tag.setFloat("Accuracy", this.accuracy);
        if (this.projectiles != 1) tag.setInteger("Projectiles", this.projectiles);
        if (!this.fireCommand.isEmpty()) tag.setString("FireCommand", this.fireCommand);
        if (!this.tickCommand.isEmpty()) tag.setString("TickCommand", this.tickCommand);
        if (!this.impactCommand.isEmpty()) tag.setString("ImpactCommand", this.impactCommand);
        if (this.ticking != 0) tag.setInteger("Ticking", this.ticking);
        if (this.lifeSpan != 200) tag.setInteger("LifeSpan", this.lifeSpan);
        if (this.speed != 0.1F) tag.setFloat("Speed", this.speed);
        if (this.friction != 0.95F) tag.setFloat("Friction", this.friction);
        if (this.gravity != 0.01F) tag.setFloat("Gravity", this.gravity);
        if (!this.vanish) tag.setBoolean("Vanish", this.vanish);
        if (this.damage != 0) tag.setFloat("Damage", this.damage);

        return tag;
    }

    private NBTTagCompound to(AbstractMorph morph)
    {
        NBTTagCompound tag = new NBTTagCompound();
        morph.toNBT(tag);

        return tag;
    }

    @SideOnly(Side.CLIENT)
    public void update()
    {
        if (this.entity != null)
        {
            this.entity.ticksExisted++;

            if (this.defaultMorph != null)
            {
                this.defaultMorph.update(this.entity, null);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void render(float partialTicks)
    {
        if (this.entity == null)
        {
            this.createEntity();
        }

        if (this.defaultMorph != null && this.entity != null)
        {
            this.setupEntity();
            this.defaultMorph.render(this.entity, 0.5F, 0, 0.5F, 0, partialTicks);
        }
    }

    @SideOnly(Side.CLIENT)
    public void renderProjectile(float partialTicks)
    {
        if (this.entity == null)
        {
            this.createEntity();
        }

        if (this.projectileMorph != null && this.entity != null)
        {
            this.setupEntity();
            this.projectileMorph.render(this.entity, 0, 0, 0, 0, partialTicks);
        }
    }

    @SideOnly(Side.CLIENT)
    private void setupEntity()
    {
        this.entity.setPositionAndRotation(0.5F, 0, 0.5F, 0, 0);
        this.entity.setLocationAndAngles(0.5F, 0, 0.5F, 0, 0);
        this.entity.rotationYawHead = this.entity.prevRotationYawHead = 0;
        this.entity.rotationYaw = this.entity.prevRotationYaw = 0;
        this.entity.rotationPitch = this.entity.prevRotationPitch = 0;
        this.entity.renderYawOffset = this.entity.prevRenderYawOffset = 0;
        this.entity.setVelocity(0, 0, 0);
    }
}