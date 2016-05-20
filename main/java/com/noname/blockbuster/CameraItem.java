package com.noname.blockbuster;

import net.minecraft.block.BlockFence;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityZombie;

/**
 * Camera item
 * 
 * Spawns a rideable camera entity (for controlling the camera)
 */
public class CameraItem extends Item 
{
	public CameraItem()
	{
		setMaxStackSize(1);
	}
	
	/**
	 * Spawns a camera 
	 */
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote)
        {
            return EnumActionResult.SUCCESS;
        }
		
        EntityLiving zombie = new EntityZombie(worldIn);
        
        pos = pos.offset(facing);
        
        zombie.setLocationAndAngles(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, 0.0F, 0.0F);
        zombie.rotationYawHead = zombie.rotationYaw;
        zombie.renderYawOffset = zombie.rotationYaw;
            	
        worldIn.spawnEntityInWorld(zombie);
		
		return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
}