package com.noname.blockbuster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class CameraEntity extends EntityCreature 
{
	/** Speed of camera */
	public float speed = 0.4F;
	
	public CameraEntity(World worldIn) 
	{
		super(worldIn);
		setSize(0.9F, 0.9F);
	}
	
	@Override
	public boolean isEntityInvulnerable(DamageSource source) 
	{
		return true;
	}
	
	/* Riding logic */
	
	public Entity getControllingPassenger()
    {
        return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
    }
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand p_184645_2_, ItemStack stack)
    {
		if (!worldObj.isRemote && !isBeingRidden()) {
			player.startRiding(this);
			
			return true;
		}
		
        return false;
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
            strafe = player.moveStrafing;
            
            boolean oldOnGround = this.onGround;
            float flyingMotion = forward != 0 ? -player.rotationPitch / 90.0F : 0.0F;
            
            forward = flyingMotion == 0 ? forward : forward * (1 - Math.abs(flyingMotion));
            
            prevRotationYaw = rotationYaw = player.rotationYaw;
            rotationPitch = player.rotationPitch * 0.5F;
            rotationYawHead = renderYawOffset = rotationYaw;
            setRotation(rotationYaw, rotationPitch);
            
            motionY = flyingMotion * Math.copySign(1.0F, forward);
            
            /* Hacks */
            onGround = true;
            setAIMoveSpeed(speed);
            super.moveEntityWithHeading(strafe, forward);
            onGround = oldOnGround;
            
            System.out.println(forward);
        }
	}
}
