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
    public AbstractMorph hands;
    public AbstractMorph guiMorph;
    public AbstractMorph reloadMorph;
    public AbstractMorph morph_overlay;
    public AbstractMorph firingMorph;
    public AbstractMorph aimMorph;
    public String fireCommand;
    public int delay;
    public boolean enableCustomGuiMorph;
    public boolean needToBeReloaded;
    public int projectiles;
    public long reloadTick;
    public float scatterX;
    public float scatterY;
    public boolean launch;
    public boolean useTarget;
    public ItemStack ammoStack = ItemStack.EMPTY;
    public String reloadCommand;
    public String zoomOnCommand;
    public String zoomOffCommand;
    public float zoom;
    public boolean hideHandOnZoom;
    public boolean hideAimOnZoom;
    public long inputReloadingTime;
    public long timeBetweenShoot;
    public long inputTimeBetweenShoot;
    public int innerAmmo;
    public int inputAmmo;
    public boolean hand_bow;
    public boolean hand_bow_always;
    public float srcShootX;
    public float srcShootY;
    public float srcShootZ;
    public float recoilXMin;
    public float recoilXMax;
    public float recoilYMin;
    public float recoilYMax;
    public boolean acceptPressed;
    public boolean recoilSimple;
    public boolean resetTimerButtonRel;
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
    public boolean enableOverlay;
    /* Impact properties */
    public AbstractMorph impactMorph;
    public String impactCommand;
    public String impactEntityCommand;
    public int impactDelay;
    public boolean vanish;
    public boolean bounce;
    public boolean sticks;
    public int hits;
    public int state;
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
    private Morph current_hands = new Morph();
    private Morph current_guiMorph = new Morph();
    private Morph current_overlay = new Morph();
    private Morph current_reloadMorph = new Morph();
    public Morph current_aim = new Morph();
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
    public void setCurrentOverlay(AbstractMorph morph)
    {
        this.current_overlay.setDirect(morph);
    }
    public void setHands(AbstractMorph morph){
        this.current_hands.setDirect(morph);
    }
    public void setAimMorph(AbstractMorph morph){

        this.current_aim.setDirect(morph);
    }
    public void setGuiMorph(AbstractMorph morph){
        this.current_guiMorph.setDirect(morph);
    }
    public void setReloadMorph(AbstractMorph morph){
        this.current_reloadMorph.setDirect(morph);
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
    public void renderOverlay(EntityLivingBase lastItemHolder, float partialTicks) {
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
        AbstractMorph morph = this.current_overlay.get();

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
           // this.gunTransform.transform();

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
            if (getGUNState() == ItemGun.GunState.RELOADING){
                AbstractMorph reloadMorph = this.current_reloadMorph.get();
                if(reloadMorph != null) {
                    MorphUtils.render(reloadMorph, entity, 0, 0, 0, 0, partialTicks);
                }else {
                    MorphUtils.render(morph, entity, 0, 0, 0, 0, partialTicks);

                }

            }else {
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
    public void renderGUIMorph(EntityLivingBase target, float partialTicks){
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
        AbstractMorph morph = this.current_guiMorph.get();
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
        AbstractMorph morph = this.current_hands.get();

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
        this.hands = null;
        this.guiMorph = null;
        this.reloadMorph = null;
        this.aimMorph = null;
        this.morph_overlay = null;
        this.firingMorph = null;
        this.fireCommand = "";
        this.delay = 0;
        this.projectiles = 1;
        this.innerAmmo=1;
        this.inputReloadingTime = 0;
        this.timeBetweenShoot = 0;
        this.inputTimeBetweenShoot=0;
        this.inputAmmo = 1;
        this.reloadTick=0;
        this.scatterX = this.scatterY = 0F;
        this.launch = false;
        this.enableCustomGuiMorph = false;
        this.useTarget = false;
        this.ammoStack = ItemStack.EMPTY;
        this.zoom = 0;
        this.recoilXMin = 0;
        this.srcShootX = 0;
        this.srcShootY = 0;
        this.srcShootZ = 0;
        this.recoilSimple = true;
        this.resetTimerButtonRel = true;
        this.recoilXMax = 0;
        this.recoilYMin = 0;
        this.recoilYMax = 0;
        /* Projectile properties */
        this.projectileMorph = null;
        this.tickCommand = "";
        this.zoomOffCommand = "";
        this.zoomOnCommand = "";
        this.reloadCommand = "";
        this.ticking = 0;
        this.lifeSpan = 200;
        this.yaw = true;
        this.enableOverlay = false;
        this.hideHandOnZoom = false;
        this.hideAimOnZoom = false;
        this.hand_bow = false;
        this.acceptPressed = true;
        this.hand_bow_always = false;
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
        this.state = 0;
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
        this.hands = this.create(tag, "Hands");
        this.guiMorph = this.create(tag,"GUIMorph");
        this.reloadMorph = this.create(tag,"ReloadMorph");
        this.aimMorph = this.create(tag,"AimMorph");
        this.morph_overlay = this.create(tag, "OverlayMorph");
        this.firingMorph = this.create(tag, "Fire");
        if (tag.hasKey("FireCommand")) this.fireCommand = tag.getString("FireCommand");
        if (tag.hasKey("Delay")) this.delay = tag.getInteger("Delay");


        if (tag.hasKey("Projectiles")) this.projectiles = tag.getInteger("Projectiles");
        if (tag.hasKey("reloadTick")) this.reloadTick = tag.getInteger("reloadTick");



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
        if (tag.hasKey("enableCustomGuiMorph")) this.enableCustomGuiMorph = tag.getBoolean("enableCustomGuiMorph");
        if (tag.hasKey("needToBeReloaded")) this.needToBeReloaded = tag.getBoolean("needToBeReloaded");


        if (tag.hasKey("Target")) this.useTarget = tag.getBoolean("Target");
        if (tag.hasKey("AmmoStack")) this.ammoStack = new ItemStack(tag.getCompoundTag("AmmoStack"));

        /* Projectile properties */
        this.projectileMorph = this.create(tag, "Projectile");
        if (tag.hasKey("TickCommand")) this.tickCommand = tag.getString("TickCommand");
        if (tag.hasKey("reloadCommand")) this.reloadCommand = tag.getString("reloadCommand");
        if (tag.hasKey("zoomOnCommand")) this.zoomOnCommand = tag.getString("zoomOnCommand");
        if (tag.hasKey("zoomOffCommand")) this.zoomOffCommand = tag.getString("zoomOffCommand");

        
        if (tag.hasKey("Ticking")) this.ticking = tag.getInteger("Ticking");
        if (tag.hasKey("innerAmmo")) this.innerAmmo = tag.getInteger("innerAmmo");
        if (tag.hasKey("inputReloadingTime")) this.inputReloadingTime = tag.getInteger("inputReloadingTime");
        if (tag.hasKey("timeBetweenShoot")) this.timeBetweenShoot = tag.getLong("timeBetweenShoot");
        if (tag.hasKey("inputTimeBetweenShoot")) this.inputTimeBetweenShoot = tag.getLong("inputTimeBetweenShoot");



        if (tag.hasKey("inputAmmo")) this.inputAmmo = tag.getInteger("inputAmmo");

        if (tag.hasKey("LifeSpan")) this.lifeSpan = tag.getInteger("LifeSpan");
        if (tag.hasKey("Yaw")) this.yaw = tag.getBoolean("Yaw");
        if (tag.hasKey("EnableOverlay")) this.enableOverlay = tag.getBoolean("EnableOverlay");
        if (tag.hasKey("hideHandOnZoom")) this.hideHandOnZoom = tag.getBoolean("hideHandOnZoom");
        if (tag.hasKey("hideAimOnZoom")) this.hideAimOnZoom = tag.getBoolean("hideAimOnZoom");


        if (tag.hasKey("acceptPressed")) this.acceptPressed = tag.getBoolean("acceptPressed");


        if (tag.hasKey("hand_bow")) this.hand_bow = tag.getBoolean("hand_bow");
        if (tag.hasKey("hand_bow_always")) this.hand_bow_always = tag.getBoolean("hand_bow_always");


        if (tag.hasKey("Pitch")) this.pitch = tag.getBoolean("Pitch");
        if (tag.hasKey("Sequencer")) this.sequencer = tag.getBoolean("Sequencer");
        if (tag.hasKey("Random")) this.random = tag.getBoolean("Random");
        if (tag.hasKey("HX")) this.hitboxX = tag.getFloat("HX");
        if (tag.hasKey("HY")) this.hitboxY = tag.getFloat("HY");
        if (tag.hasKey("Speed")) this.speed = tag.getFloat("Speed");
        if (tag.hasKey("Zoom")) this.zoom = tag.getFloat("Zoom");
        if (tag.hasKey("recoilXMin")) this.recoilXMin = tag.getFloat("recoilXMin");
        if (tag.hasKey("srcShootX")) this.srcShootX = tag.getFloat("srcShootX");
        if (tag.hasKey("srcShootY")) this.srcShootY = tag.getFloat("srcShootY");
        if (tag.hasKey("srcShootZ")) this.srcShootZ = tag.getFloat("srcShootZ");


        if (tag.hasKey("recoilSimple")) this.recoilSimple = tag.getBoolean("recoilSimple");
        
        if (tag.hasKey("resetTimerAfterHandOff")) this.resetTimerButtonRel = tag.getBoolean("resetTimerAfterHandOff");
    
        if (tag.hasKey("recoilXMax")) this.recoilXMax = tag.getFloat("recoilXMax");
        if (tag.hasKey("recoilYMin")) this.recoilYMin = tag.getFloat("recoilYMin");
        if (tag.hasKey("recoilYMax")) this.recoilYMax = tag.getFloat("recoilYMax");

        if (tag.hasKey("Friction")) this.friction = tag.getFloat("Friction");
        if (tag.hasKey("Gravity")) this.gravity = tag.getFloat("Gravity");
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
        if (tag.hasKey("state")) this.state = tag.getInteger("state");


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
            this.current_hands.set(MorphUtils.copy(this.hands));
            this.current_overlay.set(MorphUtils.copy(this.morph_overlay));
            this.current_guiMorph.set(MorphUtils.copy(this.guiMorph));
            this.current_reloadMorph.set(MorphUtils.copy(this.reloadMorph));
            this.current_aim.set(MorphUtils.copy(this.aimMorph));
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
        if (this.hands != null) tag.setTag("Hands", this.to(this.hands));
        if (this.guiMorph !=null) tag.setTag("GUIMorph", this.to(this.guiMorph));
        if (this.reloadMorph !=null) tag.setTag("ReloadMorph", this.to(this.reloadMorph));
        if (this.aimMorph !=null) tag.setTag("AimMorph",this.to(this.aimMorph));

        if (this.morph_overlay != null) tag.setTag("OverlayMorph", this.to(this.morph_overlay));
        if (this.firingMorph != null) tag.setTag("Fire", this.to(this.firingMorph));
        if (!this.fireCommand.isEmpty()) tag.setString("FireCommand", this.fireCommand);
        if (this.delay != 0) tag.setInteger("Delay", this.delay);

        if (this.projectiles != 1) tag.setInteger("Projectiles", this.projectiles);
        if (this.reloadTick !=0) tag.setLong("reloadTick",this.reloadTick);

        if (this.scatterX != 0F || this.scatterY != 0F)
        {
            NBTTagList scatter = new NBTTagList();

            scatter.appendTag(new NBTTagFloat(this.scatterX));
            scatter.appendTag(new NBTTagFloat(this.scatterY));

            tag.setTag("Scatter", scatter);
        }
        if (this.launch) tag.setBoolean("Launch", this.launch);
        if (this.enableCustomGuiMorph) tag.setBoolean("enableCustomGuiMorph",this.enableCustomGuiMorph);
        if (this.needToBeReloaded) tag.setBoolean("needToBeReloaded",this.needToBeReloaded);


        if (this.useTarget) tag.setBoolean("Target", this.useTarget);
        if (!this.ammoStack.isEmpty()) tag.setTag("AmmoStack", this.ammoStack.writeToNBT(new NBTTagCompound()));

        /* Projectile properties */
        if (this.projectileMorph != null) tag.setTag("Projectile", this.to(this.projectileMorph));
        if (!this.tickCommand.isEmpty()) tag.setString("TickCommand", this.tickCommand);
    
        if (!this.reloadCommand.isEmpty()) tag.setString("reloadCommand", this.reloadCommand);
        if (!this.zoomOnCommand.isEmpty()) tag.setString("zoomOnCommand", this.zoomOnCommand);
        if (!this.zoomOffCommand.isEmpty()) tag.setString("zoomOffCommand", this.zoomOffCommand);
        
        if (this.ticking != 0) tag.setInteger("Ticking", this.ticking);
        if (this.inputAmmo != 1) tag.setInteger("inputAmmo", this.inputAmmo);
        tag.setInteger("innerAmmo", this.innerAmmo);

        if (this.inputReloadingTime != 0) tag.setLong("inputReloadingTime", this.inputReloadingTime);
         tag.setLong("timeBetweenShoot", this.timeBetweenShoot);
        if (this.inputTimeBetweenShoot != 0) tag.setLong("inputTimeBetweenShoot", this.inputTimeBetweenShoot);



        if (this.lifeSpan != 200) tag.setInteger("LifeSpan", this.lifeSpan);
        if (!this.yaw) tag.setBoolean("Yaw", this.yaw);
        if (this.enableOverlay) tag.setBoolean("EnableOverlay", this.enableOverlay);
        if (this.hideHandOnZoom) tag.setBoolean("hideHandOnZoom", this.hideHandOnZoom);

        if (this.hideAimOnZoom) tag.setBoolean("hideAimOnZoom", this.hideAimOnZoom);


        if (!this.acceptPressed) tag.setBoolean("acceptPressed", this.acceptPressed);


        if (this.hand_bow_always) tag.setBoolean("hand_bow_always", this.hand_bow_always);
        if (this.hand_bow) tag.setBoolean("hand_bow", this.hand_bow);


        if (!this.pitch) tag.setBoolean("Pitch", this.pitch);
        if (this.sequencer) tag.setBoolean("Sequencer", this.sequencer);
        if (this.random) tag.setBoolean("Random", this.random);
        if (this.hitboxX != 0.25F) tag.setFloat("HX", this.hitboxX);
        if (this.hitboxY != 0.25F) tag.setFloat("HY", this.hitboxY);
        if (this.zoom != 0) tag.setFloat("Zoom", this.zoom);
        if (this.recoilXMin != 0) tag.setFloat("recoilXMin", this.recoilXMin);
        if (this.srcShootX != 0) tag.setFloat("srcShootX", this.srcShootX);
        if (this.srcShootY != 0) tag.setFloat("srcShootY", this.srcShootY);
        if (this.srcShootZ != 0) tag.setFloat("srcShootZ", this.srcShootZ);


        if (!this.recoilSimple) tag.setBoolean("recoilSimple", this.recoilSimple);
    
        
        if (!this.resetTimerButtonRel) tag.setBoolean("resetTimerAfterHandOff", this.resetTimerButtonRel);
    
        
        if (this.recoilXMax != 0) tag.setFloat("recoilXMax", this.recoilXMax);
        if (this.recoilYMin != 0) tag.setFloat("recoilYMin", this.recoilYMin);
        if (this.recoilYMax != 0) tag.setFloat("recoilYMax", this.recoilYMax);
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
        tag.setInteger("state", this.state);


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

    public void setGUNState(ItemGun.GunState state)
    {
        switch (state){
            case UNDEF:
                this.state = -1;
                return;
            case RELOADING:
                this.state = 2;
                return;
            case NEED_TO_BE_RELOAD:
                this.state = 1;
                return;
            case READY_TO_SHOOT:
                this.state = 0;
                return;
            default:
                this.state = -1;
        }
    }

    public ItemGun.GunState getGUNState()
    {
     
     switch (this.state)
     {
         case 0:
             return ItemGun.GunState.READY_TO_SHOOT;
         case 1:
             return ItemGun.GunState.NEED_TO_BE_RELOAD;
         case 2:
             return ItemGun.GunState.RELOADING;
         default:
             return ItemGun.GunState.UNDEF;
     }
     
    }

}