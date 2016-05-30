package noname.blockbuster.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.item.CameraConfigItem;

public class CameraEntity extends EntityLiving
{
	public float speed = 0.4F;
	public float accelerationRate = 0.02F;
	public float accelerationMax = 1.5f;
	public boolean canFly = true;
	
	protected float acceleration = 0.0F;
	
	public CameraEntity(World worldIn) 
	{
		super(worldIn);
		setSize(0.9F, 0.9F);
	}
	
	/** 
	 * No knockback is allowed 
	 */
	@Override
	public void knockBack(Entity entityIn, float magnitued, double a, double b) {}
	
	@Override
	public double getMountedYOffset() 
	{
		return this.height * 0.3;
	}
	
	@Override
	protected void applyEntityAttributes() 
	{
		super.applyEntityAttributes();
		
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
	}
	
	/**
	 * Camera is invincible against fall damage
	 */
	@Override
	public boolean isEntityInvulnerable(DamageSource source) 
	{
		return source == DamageSource.fall;
	}
	
	/* Riding logic */
	
	/**
	 * Processes player's right clicking on the entity
	 * 
	 * If the player holds camera configuration item, then GUI with camera configuration properties 
	 * should pop up, otherwise start riding
	 */
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand p_184645_2_, ItemStack stack)
    {
		ItemStack item = player.getHeldItemMainhand();
		
		if (item != null && item.getItem() instanceof CameraConfigItem)
		{
			if (worldObj.isRemote) 
			{
				player.openGui(Blockbuster.instance, 0, worldObj, getEntityId(), 0, 0);
			}
			
			return true;
		}
		
		if (!worldObj.isRemote)
		{
			if (!isBeingRidden())
			{
				return player.startRiding(this);
			}
		}
		
        return false;
    }
	
	/**
	 * Totally not taken from EntityPig class
	 */
	@Override
	public Entity getControllingPassenger()
    {
        return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
    }
	
	@Override
	public boolean canBeSteered() 
	{
		return true;
	}
	
	/**
	 * Totally not copy-pasted from EntityHorse/AnimalBikes classes
	 */
	@Override
	public void moveEntityWithHeading(float strafe, float forward) 
	{
		if (isBeingRidden())
        {
            EntityLivingBase player = (EntityLivingBase)this.getControllingPassenger();
            
            forward = player.moveForward;
            strafe = player.moveStrafing * 0.65F;
            
            boolean oldOnGround = this.onGround;
            float flyingMotion = forward != 0 ? -player.rotationPitch / 90.0F : 0.0F;
            
            prevRotationYaw = rotationYaw = player.rotationYaw;
            prevRotationPitch = rotationPitch = player.rotationPitch;
            rotationYawHead = renderYawOffset = rotationYaw;
            setRotation(rotationYaw, rotationPitch);
            
            /* Acceleration logic */
            if (strafe != 0 || forward != 0) 
            {
            	acceleration = MathHelper.clamp_float(acceleration + accelerationRate, 0.0F, accelerationMax);
            	
            	forward *= acceleration;
            	strafe *= acceleration;
            }
            else 
            {
            	acceleration = 0.0F;
            }
            
            System.out.println(forward + " " + flyingMotion);
            
            /* Flying logic */
            if (canFly)
            {
            	forward = flyingMotion == 0 ? forward : forward * (1 - Math.abs(flyingMotion));
            	motionY = flyingMotion * acceleration * Math.copySign(1.0F, forward);
            }
            
            /* Hacks */
            onGround = true;
            setAIMoveSpeed(speed);
            super.moveEntityWithHeading(strafe, forward);
            onGround = oldOnGround;
        }
	}
}
