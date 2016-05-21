package com.noname.blockbuster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CameraEntity extends EntityCreature 
{
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
	
	/** Riding logic */
	
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
		if (isBeingRidden() && canBeSteered())
        {
            EntityLivingBase player = (EntityLivingBase)this.getControllingPassenger();
            
            this.prevRotationYaw = this.rotationYaw = player.rotationYaw;
            this.rotationPitch = player.rotationPitch * 0.5F;
            this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            
            strafe = player.moveStrafing * 0.85F;
            forward = player.moveForward;

            if (this.canPassengerSteer())
            {
                this.setAIMoveSpeed(0.22F + (player.isSprinting() ? 0.1F : 0.0F));
                super.moveEntityWithHeading(strafe, forward);
            }
        }
        else
        {
            super.moveEntityWithHeading(strafe, forward);
        }
	}
}
