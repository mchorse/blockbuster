package noname.blockbuster.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.world.World;

/**
 * Actor entity class
 * 
 * Actor entity class is responsible for recording player's actions and 
 * execute them. I'm also thinking about giving them controllable AI settings
 * so they could be used without recording (like during the battles between two or more actors).
 */
public class ActorEntity extends EntityCreature
{
	public ActorEntity(World worldIn) 
	{
		super(worldIn);
	}
}
