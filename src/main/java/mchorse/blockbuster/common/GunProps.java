package mchorse.blockbuster.common;

import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.entity.EntityGunProjectile;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.metamorph.api.Morph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Blockbuster gun properties
 * <p>
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
    public float scatterX;
    public float scatterY;
    public boolean launch;
    public boolean useTarget;
    public ItemStack ammoStack = ItemStack.EMPTY;

    /* Evanechecssss' options */
    public boolean staticRecoil;
    public float recoilXMin;
    public float recoilXMax;
    public float recoilYMin;
    public float recoilYMax;

    public boolean enableArmsShootingPose;
    public boolean alwaysArmsShootingPose;

    public float shootingOffsetX;
    public float shootingOffsetY;
    public float shootingOffsetZ;

    public AbstractMorph inventoryMorph;
    public AbstractMorph crosshairMorph;
    public AbstractMorph handsMorph;
    public AbstractMorph reloadMorph;
    public AbstractMorph zoomOverlayMorph;

    public boolean hideCrosshairOnZoom;
    public boolean useInventoryMorph;
    public boolean hideHandsOnZoom;
    public boolean useZoomOverlayMorph;

    public float zoomFactor;
    public int ammo;
    public boolean useReloading;
    public long reloadingTime;
    public long shotDelay;
    public boolean shootWhenHeld;

    public String destroyCommand;
    public String meleeCommand;
    public String reloadCommand;
    public String zoomOnCommand;
    public String zoomOffCommand;

    public float meleeDamage;
    public float mouseZoom;
    public int durability;
    public boolean preventLeftClick;
    public boolean preventRightClick;
    public boolean preventEntityAttack;

    public int storedAmmo;
    public long storedReloadingTime;
    public long storedShotDelay;
    public int storedDurability;
    public ItemGun.GunState state = ItemGun.GunState.READY_TO_SHOOT;

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
    public String impactEntityCommand;
    public int impactDelay;
    public boolean vanish;
    public boolean bounce;
    public boolean sticks;
    public int hits;
    public float damage;
    public float knockbackHorizontal;
    public float knockbackVertical;
    public float bounceFactor;
    public String vanishCommand;
    public int vanishDelay;
    public float penetration;
    public boolean ignoreBlocks;
    public boolean ignoreEntities;

    /* Transforms */
    public ModelTransform gunTransform = new ModelTransform();
    public ModelTransform projectileTransform = new ModelTransform();

    private int shoot = 0;
    private Morph current = new Morph();
    private Morph currentHands = new Morph();
    private Morph currentInventory = new Morph();
    private Morph currentZoomOverlay = new Morph();
    private Morph currentReload = new Morph();
    public Morph currentCrosshair = new Morph();
    private boolean renderLock;

    public EntityLivingBase target;

    public GunProps()
    {
        this.reset();
    }

    public GunProps(NBTTagCompound tag)
    {
        this.fromNBT(tag);
    }

    public void setCurrent(AbstractMorph morph)
    {
        this.current.setDirect(morph);
    }

    public void setCurrentZoomOverlay(AbstractMorph morph)
    {
        this.currentZoomOverlay.setDirect(morph);
    }

    public void setHandsMorph(AbstractMorph morph)
    {
        this.currentHands.setDirect(morph);
    }

    public void setCrosshairMorph(AbstractMorph morph)
    {
        this.currentCrosshair.setDirect(morph);
    }

    public void setInventoryMorph(AbstractMorph morph)
    {
        this.currentInventory.setDirect(morph);
    }

    public void setReloadMorph(AbstractMorph morph)
    {
        this.currentReload.setDirect(morph);
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
        if (this.target != null)
        {
            return;
        }

        this.target = new EntityActor(world);
        this.target.onGround = true;
        this.target.rotationYaw = this.target.prevRotationYaw = 0;
        this.target.rotationYawHead = this.target.prevRotationYawHead = 0;
        this.target.rotationPitch = this.target.prevRotationPitch = 0;
    }

    public EntityLivingBase getEntity(EntityGunProjectile entity)
    {
        if (this.target != null)
        {
            this.target.prevPosX = entity.prevPosX;
            this.target.prevPosY = entity.prevPosY;
            this.target.prevPosZ = entity.prevPosZ;

            this.target.posX = entity.posX;
            this.target.posY = entity.posY;
            this.target.posZ = entity.posZ;
        }

        return this.target;
    }

    @SideOnly(Side.CLIENT)
    public void update()
    {
        if (this.target != null)
        {
            this.target.ticksExisted++;

            AbstractMorph morph = this.current.get();
            if (morph != null)
            {
                morph.update(this.target);
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
    public void renderZoomOverlay(EntityLivingBase lastItemHolder, float partialTicks)
    {
        if (this.renderLock)
        {
            return;
        }

        this.renderLock = true;

        if (this.target == null)
        {
            this.createEntity();
        }

        EntityLivingBase entity = this.useTarget ? target : this.target;
        AbstractMorph morph = this.currentZoomOverlay.get();

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
    public void render(EntityLivingBase target, float partialTicks)
    {
        if (this.renderLock)
        {
            return;
        }

        this.renderLock = true;

        if (this.target == null)
        {
            this.createEntity();
        }

        EntityLivingBase entity = this.useTarget ? target : this.target;
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

            if (this.state == ItemGun.GunState.RELOADING)
            {
                AbstractMorph reloadMorph = this.currentReload.get();

                MorphUtils.render(reloadMorph == null ? morph : reloadMorph, entity, 0, 0, 0, 0, partialTicks);
            }
            else
            {
                MorphUtils.render(morph, entity, 0, 0, 0, 0, partialTicks);
            }
            GL11.glPopMatrix();

            entity.renderYawOffset = rotationYaw;
            entity.prevRenderYawOffset = prevRotationYaw;
            entity.rotationYawHead = rotationYawHead;
            entity.prevRotationYawHead = prevRotationYawHead;
        }

        this.renderLock = false;
    }

    @SideOnly(Side.CLIENT)
    public void renderInventoryMorph(EntityLivingBase target, float partialTicks)
    {
        if (this.renderLock)
        {
            return;
        }

        this.renderLock = true;

        if (this.target == null)
        {
            this.createEntity();
        }

        EntityLivingBase entity = this.useTarget ? target : this.target;
        AbstractMorph morph = this.currentInventory.get();

        if (morph != null && entity != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.5F, 0, 0.5F);
            this.setupEntity();
            MorphUtils.render(morph, entity, 0, 0, 0, 0, partialTicks);
            GL11.glPopMatrix();
        }

        this.renderLock = false;
    }

    @SideOnly(Side.CLIENT)
    public void renderHands(EntityLivingBase target, float partialTicks)
    {
        if (this.renderLock)
        {
            return;
        }

        this.renderLock = true;

        if (this.target == null)
        {
            this.createEntity();
        }

        EntityLivingBase entity = this.useTarget ? target : this.target;
        AbstractMorph morph = this.currentHands.get();

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
        this.target.setPositionAndRotation(0, 0, 0, 0, 0);
        this.target.setLocationAndAngles(0, 0, 0, 0, 0);
        this.target.rotationYawHead = this.target.prevRotationYawHead = 0;
        this.target.rotationYaw = this.target.prevRotationYaw = 0;
        this.target.rotationPitch = this.target.prevRotationPitch = 0;
        this.target.renderYawOffset = this.target.prevRenderYawOffset = 0;
        this.target.setVelocity(0, 0, 0);
    }

    /**
     * Reset properties to default values
     */
    public void reset()
    {
        /* Gun properties */
        this.defaultMorph = null;
        this.handsMorph = null;
        this.inventoryMorph = null;
        this.reloadMorph = null;
        this.crosshairMorph = null;
        this.zoomOverlayMorph = null;
        this.firingMorph = null;
        this.fireCommand = "";
        this.delay = 0;
        this.projectiles = 1;
        /* McHorse: screw organizing this */
        this.storedAmmo = 1;
        this.reloadingTime = 0;
        this.storedShotDelay = 0;
        this.shotDelay = 0;
        this.ammo = 1;
        this.storedReloadingTime = 0;
        this.scatterX = this.scatterY = 0F;
        this.launch = false;
        this.useInventoryMorph = false;
        this.useTarget = false;
        this.ammoStack = ItemStack.EMPTY;
        this.zoomFactor = 0;
        this.recoilXMin = 0;
        this.shootingOffsetX = 0;
        this.mouseZoom = 0.5f;
        this.meleeDamage = 0;
        this.shootingOffsetY = 0;
        this.shootingOffsetZ = 0;
        this.staticRecoil = true;
        this.recoilXMax = 0;
        this.recoilYMin = 0;
        this.recoilYMax = 0;
        /* Projectile properties */
        this.projectileMorph = null;
        this.tickCommand = "";
        this.zoomOffCommand = "";
        this.zoomOnCommand = "";
        this.reloadCommand = "";
        this.meleeCommand = "";
        this.destroyCommand = "";
        this.ticking = 0;
        this.durability = 0;
        this.storedDurability = 0;
        this.lifeSpan = 200;
        this.yaw = true;
        this.useZoomOverlayMorph = false;
        this.hideHandsOnZoom = false;
        this.hideCrosshairOnZoom = false;
        this.enableArmsShootingPose = false;
        this.preventRightClick = false;
        this.preventLeftClick = false;
        this.preventEntityAttack = false;
        this.shootWhenHeld = true;
        this.alwaysArmsShootingPose = false;
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
        this.impactEntityCommand = "";
        this.impactDelay = 0;
        this.vanish = true;
        this.bounce = false;
        this.sticks = false;
        this.hits = 1;
        this.state = ItemGun.GunState.READY_TO_SHOOT;
        this.damage = 0F;
        this.knockbackHorizontal = 0F;
        this.knockbackVertical = 0F;
        this.bounceFactor = 1F;
        this.vanishCommand = "";
        this.vanishDelay = 0;
        this.penetration = 0;
        this.ignoreBlocks = false;
        this.ignoreEntities = false;

        /* Transforms */
        this.gunTransform = new ModelTransform();
        this.projectileTransform = new ModelTransform();
    }

    public void fromNBT(NBTTagCompound tag)
    {
        this.reset();

        /* Gun properties */
        this.defaultMorph = this.create(tag, "Morph");
        this.handsMorph = this.create(tag, "HandsMorph");
        this.inventoryMorph = this.create(tag, "InventoryMorph");
        this.reloadMorph = this.create(tag, "ReloadMorph");
        this.crosshairMorph = this.create(tag, "CrosshairMorph");
        this.zoomOverlayMorph = this.create(tag, "ZoomOverlayMorph");
        this.firingMorph = this.create(tag, "Fire");

        if (tag.hasKey("FireCommand")) this.fireCommand = tag.getString("FireCommand");
        if (tag.hasKey("Delay")) this.delay = tag.getInteger("Delay");

        if (tag.hasKey("Projectiles")) this.projectiles = tag.getInteger("Projectiles");
        if (tag.hasKey("StoredReloadingTime")) this.storedReloadingTime = tag.getInteger("StoredReloadingTime");

        if (tag.hasKey("Scatter"))
        {
            NBTBase scatter = tag.getTag("Scatter");

            if (scatter instanceof NBTTagList)
            {
                NBTTagList list = (NBTTagList) scatter;

                if (list.tagCount() >= 2)
                {
                    this.scatterX = list.getFloatAt(0);
                    this.scatterY = list.getFloatAt(1);
                }
            }
            else
            {
                /* Old compatibility scatter */
                this.scatterX = this.scatterY = tag.getFloat("Scatter");
            }
        }
        if (tag.hasKey("ScatterY")) this.scatterY = tag.getFloat("ScatterY");
        if (tag.hasKey("Launch")) this.launch = tag.getBoolean("Launch");
        if (tag.hasKey("UseInventoryMorph")) this.useInventoryMorph = tag.getBoolean("UseInventoryMorph");
        if (tag.hasKey("UseReloading")) this.useReloading = tag.getBoolean("UseReloading");


        if (tag.hasKey("Target")) this.useTarget = tag.getBoolean("Target");
        if (tag.hasKey("AmmoStack")) this.ammoStack = new ItemStack(tag.getCompoundTag("AmmoStack"));

        /* Projectile properties */
        this.projectileMorph = this.create(tag, "Projectile");
        if (tag.hasKey("TickCommand")) this.tickCommand = tag.getString("TickCommand");
        if (tag.hasKey("MeleeCommand")) this.meleeCommand = tag.getString("MeleeCommand");
        if (tag.hasKey("DestroyCommand")) this.destroyCommand = tag.getString("DestroyCommand");
        if (tag.hasKey("ReloadCommand")) this.reloadCommand = tag.getString("ReloadCommand");
        if (tag.hasKey("ZoomOnCommand")) this.zoomOnCommand = tag.getString("ZoomOnCommand");
        if (tag.hasKey("ZoomOffCommand")) this.zoomOffCommand = tag.getString("ZoomOffCommand");


        if (tag.hasKey("Ticking")) this.ticking = tag.getInteger("Ticking");
        if (tag.hasKey("StoredAmmo")) this.storedAmmo = tag.getInteger("StoredAmmo");
        if (tag.hasKey("ReloadingTime")) this.reloadingTime = tag.getInteger("ReloadingTime");
        if (tag.hasKey("StoredShotDelay")) this.storedShotDelay = tag.getLong("StoredShotDelay");
        if (tag.hasKey("ShotDelay")) this.shotDelay = tag.getLong("ShotDelay");


        if (tag.hasKey("Ammo")) this.ammo = tag.getInteger("Ammo");

        if (tag.hasKey("LifeSpan")) this.lifeSpan = tag.getInteger("LifeSpan");
        if (tag.hasKey("Yaw")) this.yaw = tag.getBoolean("Yaw");
        if (tag.hasKey("UseZoomOverlayMorph")) this.useZoomOverlayMorph = tag.getBoolean("UseZoomOverlayMorph");
        if (tag.hasKey("HideHandsOnZoom")) this.hideHandsOnZoom = tag.getBoolean("HideHandsOnZoom");
        if (tag.hasKey("HideCrosshairOnZoom")) this.hideCrosshairOnZoom = tag.getBoolean("HideCrosshairOnZoom");


        if (tag.hasKey("ShootWhenHeld")) this.shootWhenHeld = tag.getBoolean("ShootWhenHeld");


        if (tag.hasKey("ArmPose")) this.enableArmsShootingPose = tag.getBoolean("ArmPose");
        if (tag.hasKey("ArmPoseAlways")) this.alwaysArmsShootingPose = tag.getBoolean("ArmPoseAlways");
        if (tag.hasKey("PreventLeftClick")) this.preventLeftClick = tag.getBoolean("PreventLeftClick");
        if (tag.hasKey("PreventRightClick")) this.preventRightClick = tag.getBoolean("PreventRightClick");
        if (tag.hasKey("PreventEntityAttack")) this.preventEntityAttack = tag.getBoolean("PreventEntityAttack");


        if (tag.hasKey("Pitch")) this.pitch = tag.getBoolean("Pitch");
        if (tag.hasKey("Sequencer")) this.sequencer = tag.getBoolean("Sequencer");
        if (tag.hasKey("Random")) this.random = tag.getBoolean("Random");
        if (tag.hasKey("HX")) this.hitboxX = tag.getFloat("HX");
        if (tag.hasKey("HY")) this.hitboxY = tag.getFloat("HY");
        if (tag.hasKey("Speed")) this.speed = tag.getFloat("Speed");
        if (tag.hasKey("Zoom")) this.zoomFactor = tag.getFloat("Zoom");
        if (tag.hasKey("RecoilMinX")) this.recoilXMin = tag.getFloat("RecoilMinX");
        if (tag.hasKey("ShootingOffsetX")) this.shootingOffsetX = tag.getFloat("ShootingOffsetX");
        if (tag.hasKey("MouseZoom")) this.mouseZoom = tag.getFloat("MouseZoom");
        if (tag.hasKey("MeleeDamage")) this.meleeDamage = tag.getFloat("MeleeDamage");


        if (tag.hasKey("ShootingOffsetY")) this.shootingOffsetY = tag.getFloat("ShootingOffsetY");
        if (tag.hasKey("ShootingOffsetZ")) this.shootingOffsetZ = tag.getFloat("ShootingOffsetZ");


        if (tag.hasKey("StaticRecoil")) this.staticRecoil = tag.getBoolean("StaticRecoil");

        if (tag.hasKey("RecoilMaxX")) this.recoilXMax = tag.getFloat("RecoilMaxX");
        if (tag.hasKey("RecoilMinY")) this.recoilYMin = tag.getFloat("RecoilMinY");
        if (tag.hasKey("RecoilMaxY")) this.recoilYMax = tag.getFloat("RecoilMaxY");

        if (tag.hasKey("Friction")) this.friction = tag.getFloat("Friction");
        if (tag.hasKey("Gravity")) this.gravity = tag.getFloat("Gravity");

        if (tag.hasKey("Durability")) this.durability = tag.getInteger("Durability");
        if (tag.hasKey("StoredDurability")) this.storedDurability = tag.getInteger("StoredDurability");


        if (tag.hasKey("FadeIn")) this.fadeIn = tag.getInteger("FadeIn");
        if (tag.hasKey("FadeOut")) this.fadeOut = tag.getInteger("FadeOut");
        /* Impact properties */
        this.impactMorph = this.create(tag, "Impact");
        if (tag.hasKey("ImpactCommand")) this.impactCommand = tag.getString("ImpactCommand");
        if (tag.hasKey("ImpactEntityCommand")) this.impactEntityCommand = tag.getString("ImpactEntityCommand");
        if (tag.hasKey("ImpactDelay")) this.impactDelay = tag.getInteger("ImpactDelay");
        if (tag.hasKey("Vanish")) this.vanish = tag.getBoolean("Vanish");
        if (tag.hasKey("Bounce")) this.bounce = tag.getBoolean("Bounce");
        if (tag.hasKey("Stick")) this.sticks = tag.getBoolean("Stick");
        if (tag.hasKey("Hits")) this.hits = tag.getInteger("Hits");

        if (tag.hasKey("State")) this.state = ItemGun.GunState.values()[tag.getInteger("State")];

        if (tag.hasKey("Damage")) this.damage = tag.getFloat("Damage");
        if (tag.hasKey("KnockbackH")) this.knockbackHorizontal = tag.getFloat("KnockbackH");
        if (tag.hasKey("KnockbackV")) this.knockbackVertical = tag.getFloat("KnockbackV");
        if (tag.hasKey("BFactor")) this.bounceFactor = tag.getFloat("BFactor");
        if (tag.hasKey("VanishCommand")) this.vanishCommand = tag.getString("VanishCommand");
        if (tag.hasKey("VDelay")) this.vanishDelay = tag.getInteger("VDelay");
        if (tag.hasKey("Penetration")) this.penetration = tag.getFloat("Penetration");
        if (tag.hasKey("IBlocks")) this.ignoreBlocks = tag.getBoolean("IBlocks");
        if (tag.hasKey("IEntities")) this.ignoreEntities = tag.getBoolean("IEntities");

        /* Transforms */
        if (tag.hasKey("Gun")) this.gunTransform.fromNBT(tag.getCompoundTag("Gun"));
        if (tag.hasKey("Transform")) this.projectileTransform.fromNBT(tag.getCompoundTag("Transform"));

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
        {
            this.current.set(MorphUtils.copy(this.defaultMorph));
            this.currentHands.set(MorphUtils.copy(this.handsMorph));
            this.currentZoomOverlay.set(MorphUtils.copy(this.zoomOverlayMorph));
            this.currentInventory.set(MorphUtils.copy(this.inventoryMorph));
            this.currentReload.set(MorphUtils.copy(this.reloadMorph));
            this.currentCrosshair.set(MorphUtils.copy(this.crosshairMorph));
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
        if (this.scatterX != 0F || this.scatterY != 0F)
        {
            NBTTagList scatter = new NBTTagList();

            scatter.appendTag(new NBTTagFloat(this.scatterX));
            scatter.appendTag(new NBTTagFloat(this.scatterY));

            tag.setTag("Scatter", scatter);
        }
        if (this.launch) tag.setBoolean("Launch", this.launch);
        if (this.useTarget) tag.setBoolean("Target", this.useTarget);
        if (!this.ammoStack.isEmpty()) tag.setTag("AmmoStack", this.ammoStack.writeToNBT(new NBTTagCompound()));

        /* Evanechecssss' options */
        if (!this.staticRecoil) tag.setBoolean("StaticRecoil", this.staticRecoil);
        if (this.recoilXMin != 0) tag.setFloat("RecoilMinX", this.recoilXMin);
        if (this.recoilXMax != 0) tag.setFloat("RecoilMaxX", this.recoilXMax);
        if (this.recoilYMin != 0) tag.setFloat("RecoilMinY", this.recoilYMin);
        if (this.recoilYMax != 0) tag.setFloat("RecoilMaxY", this.recoilYMax);

        if (this.enableArmsShootingPose) tag.setBoolean("ArmPose", this.enableArmsShootingPose);
        if (this.alwaysArmsShootingPose) tag.setBoolean("ArmPoseAlways", this.alwaysArmsShootingPose);

        if (this.shootingOffsetX != 0) tag.setFloat("ShootingOffsetX", this.shootingOffsetX);
        if (this.shootingOffsetY != 0) tag.setFloat("ShootingOffsetY", this.shootingOffsetY);
        if (this.shootingOffsetZ != 0) tag.setFloat("ShootingOffsetZ", this.shootingOffsetZ);

        if (this.inventoryMorph != null) tag.setTag("InventoryMorph", this.to(this.inventoryMorph));
        if (this.crosshairMorph != null) tag.setTag("CrosshairMorph", this.to(this.crosshairMorph));
        if (this.handsMorph != null) tag.setTag("HandsMorph", this.to(this.handsMorph));
        if (this.reloadMorph != null) tag.setTag("ReloadMorph", this.to(this.reloadMorph));
        if (this.zoomOverlayMorph != null) tag.setTag("ZoomOverlayMorph", this.to(this.zoomOverlayMorph));

        if (this.hideCrosshairOnZoom) tag.setBoolean("HideCrosshairOnZoom", this.hideCrosshairOnZoom);
        if (this.useInventoryMorph) tag.setBoolean("UseInventoryMorph", this.useInventoryMorph);
        if (this.hideHandsOnZoom) tag.setBoolean("HideHandsOnZoom", this.hideHandsOnZoom);
        if (this.useZoomOverlayMorph) tag.setBoolean("UseZoomOverlayMorph", this.useZoomOverlayMorph);

        if (this.zoomFactor != 0) tag.setFloat("Zoom", this.zoomFactor);
        if (this.ammo != 1) tag.setInteger("Ammo", this.ammo);
        if (this.useReloading) tag.setBoolean("UseReloading", this.useReloading);
        if (this.reloadingTime != 0) tag.setLong("ReloadingTime", this.reloadingTime);
        if (this.shotDelay != 0) tag.setLong("ShotDelay", this.shotDelay);
        if (!this.shootWhenHeld) tag.setBoolean("ShootWhenHeld", this.shootWhenHeld);

        if (!this.destroyCommand.isEmpty()) tag.setString("DestroyCommand", this.destroyCommand);
        if (!this.meleeCommand.isEmpty()) tag.setString("MeleeCommand", this.meleeCommand);
        if (!this.reloadCommand.isEmpty()) tag.setString("ReloadCommand", this.reloadCommand);
        if (!this.zoomOnCommand.isEmpty()) tag.setString("ZoomOnCommand", this.zoomOnCommand);
        if (!this.zoomOffCommand.isEmpty()) tag.setString("ZoomOffCommand", this.zoomOffCommand);

        if (this.meleeDamage != 0) tag.setFloat("MeleeDamage", this.meleeDamage);
        if (this.mouseZoom != 0.5f) tag.setFloat("MouseZoom", this.mouseZoom);
        if (this.durability != 0) tag.setInteger("Durability", this.durability);
        if (this.preventLeftClick) tag.setBoolean("PreventLeftClick", this.preventLeftClick);
        if (this.preventRightClick) tag.setBoolean("PreventRightClick", this.preventRightClick);
        if (this.preventEntityAttack) tag.setBoolean("PreventEntityAttack", this.preventEntityAttack);

        if (this.storedAmmo != 1) tag.setInteger("StoredAmmo", this.storedAmmo);
        if (this.storedReloadingTime != 0) tag.setLong("StoredReloadingTime", this.storedReloadingTime);
        if (this.storedShotDelay != 0) tag.setLong("StoredShotDelay", this.storedShotDelay);
        if (this.storedDurability != 0) tag.setInteger("StoredDurability", this.storedDurability);
        if (this.state != ItemGun.GunState.READY_TO_SHOOT) tag.setInteger("State", this.state.ordinal());

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
        if (!this.impactEntityCommand.isEmpty()) tag.setString("ImpactEntityCommand", this.impactEntityCommand);
        if (this.impactDelay != 0) tag.setInteger("ImpactDelay", this.impactDelay);
        if (!this.vanish) tag.setBoolean("Vanish", this.vanish);
        if (this.bounce) tag.setBoolean("Bounce", this.bounce);
        if (this.sticks) tag.setBoolean("Stick", this.sticks);
        if (this.hits != 1) tag.setInteger("Hits", this.hits);
        if (this.damage != 0) tag.setFloat("Damage", this.damage);
        if (this.knockbackHorizontal != 0F) tag.setFloat("KnockbackH", this.knockbackHorizontal);
        if (this.knockbackVertical != 0F) tag.setFloat("KnockbackV", this.knockbackVertical);
        if (this.bounceFactor != 1F) tag.setFloat("BFactor", this.bounceFactor);
        if (!this.vanishCommand.isEmpty()) tag.setString("VanishCommand", this.vanishCommand);
        if (this.vanishDelay != 0) tag.setInteger("VDelay", this.vanishDelay);
        if (this.penetration != 0) tag.setFloat("Penetration", this.penetration);
        if (this.ignoreBlocks) tag.setBoolean("IBlocks", this.ignoreBlocks);
        if (this.ignoreEntities) tag.setBoolean("IEntities", this.ignoreEntities);

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