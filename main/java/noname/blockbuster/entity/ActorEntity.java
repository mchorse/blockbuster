package noname.blockbuster.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import noname.blockbuster.api.ActorRegistry;

/**
 * Actor entity class
 * 
 * Actor entity class is responsible for recording player's actions and 
 * execute them. I'm also thinking about giving them controllable AI settings
 * so they could be used without recording (like during the battles between two or more actors).
 */
public class ActorEntity extends EntityCreature
{
	/* Important instance fields */
	public boolean isPlaying = false;
	public boolean isRecording = false;
	
	public ActorEntity(World worldIn) 
	{
		super(worldIn);
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand p_184645_2_, ItemStack stack)
    {
		if (worldObj.isRemote)
		{
			return false;
		}
		
		return false;
    }
}
