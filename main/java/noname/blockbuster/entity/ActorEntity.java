package noname.blockbuster.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import noname.blockbuster.test.Action;
import noname.blockbuster.test.Mocap;

/**
 * Actor entity class
 * 
 * Actor entity class is responsible for recording player's actions and execute
 * them. I'm also thinking about giving them controllable AI settings so they
 * could be used without recording (like during the battles between two or more
 * actors).
 */
public class ActorEntity extends EntityCreature
{
	public List<Action> eventsList = Collections.synchronizedList(new ArrayList());

	public ActorEntity(World worldIn)
	{
		super(worldIn);
	}

	private void replayShootArrow(Action ma)
	{
		float f = ma.arrowCharge / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;

		if (f < 0.1D) return;
		if (f > 1.0F) f = 1.0F;

		EntityArrow entityarrow = new EntityArrow(worldObj)
		{
			@Override
			protected ItemStack getArrowStack()
			{
				return new ItemStack(Items.arrow);
			}
		};

		entityarrow.canBePickedUp = PickupStatus.ALLOWED;
		worldObj.spawnEntityInWorld(entityarrow);
	}

	/**
	 * Process the actions
	 */
	private void processActions(Action action)
	{
		ItemStack foo = null;

		switch (action.type)
		{
			case Action.SWIPE:
				swingArm(EnumHand.MAIN_HAND);
			break;

			case Action.EQUIP:
				EntityEquipmentSlot slot = Mocap.getSlotByIndex(action.armorSlot);

				if (action.armorId == -1)
				{
					setItemStackToSlot(slot, null);
				}
				else
				{
					setItemStackToSlot(slot, ItemStack.loadItemStackFromNBT(action.itemData));
				}
			break;

			case Action.DROP:
				foo = ItemStack.loadItemStackFromNBT(action.itemData);

				EntityItem ea = new EntityItem(worldObj, posX, posY - 0.30000001192092896D + getEyeHeight(), posZ, foo);
				Random rand = new Random();

				float f = 0.3F;

				ea.motionX = (-MathHelper.sin(rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(rotationPitch / 180.0F * 3.1415927F) * f);
				ea.motionZ = (MathHelper.cos(rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(rotationPitch / 180.0F * 3.1415927F) * f);
				ea.motionY = (-MathHelper.sin(rotationPitch / 180.0F * 3.1415927F) * f + 0.1F);

				f = 0.02F;
				float f1 = rand.nextFloat() * 3.1415927F * 2.0F;
				f *= rand.nextFloat();
				
				ea.motionX += Math.cos(f1) * f;
				ea.motionY += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
				ea.motionZ += Math.sin(f1) * f;
				
				worldObj.spawnEntityInWorld(ea);
			break;
			
			case Action.SHOOTARROW:
				replayShootArrow(action);
			break;
		}
	}
	
	/**
	 * Adjust the movement and limb swinging action stuff
	 */
	public void onLivingUpdate()
	{
		if (eventsList.size() > 0)
		{
			processActions(eventsList.remove(0));
		}

		updateArmSwingProgress();

		/* Taken from the EntityDragon, IDK what it does */
		if (newPosRotationIncrements > 0)
		{
			double d5 = posX + (interpTargetX - posX) / (double) newPosRotationIncrements;
			double d0 = posY + (interpTargetY - posY) / (double) newPosRotationIncrements;
			double d1 = posZ + (interpTargetZ - posZ) / (double) newPosRotationIncrements;
			double d2 = MathHelper.wrapAngleTo180_double(interpTargetYaw - (double) rotationYaw);

			rotationYaw = (float) ((double) rotationYaw + d2 / (double) newPosRotationIncrements);
			rotationPitch = (float) ((double) rotationPitch + (newPosX - (double) rotationPitch) / (double) newPosRotationIncrements);
			newPosRotationIncrements -= 1;

			setPosition(d5, d0, d1);
			setRotation(rotationYaw, rotationPitch);
		}
		else if (!isServerWorld())
		{
			motionX *= 0.98D;
			motionY *= 0.98D;
			motionZ *= 0.98D;
		}
		
		if (Math.abs(motionX) < 0.005D) motionX = 0.0D;
		if (Math.abs(motionY) < 0.005D) motionY = 0.0D;
		if (Math.abs(motionZ) < 0.005D) motionZ = 0.0D;
		
		if (!isServerWorld())
		{
			rotationYawHead = rotationYaw;
		}

		/* Taken from the EntityOtherPlayerMP, I guess */
		prevLimbSwingAmount = limbSwingAmount;

		double d0 = posX - prevPosX;
		double d1 = posZ - prevPosZ;
		float f = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 4.0F;
		
		if (f > 1.0F) f = 1.0F;

		limbSwingAmount += (f - limbSwingAmount) * 0.4F;
		limbSwing += limbSwingAmount;
	}
}
