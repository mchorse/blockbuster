package mchorse.blockbuster.recording.capturing;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * Damage control manager
 *
 * This person is responsible for managing damage control
 */
public class DamageControlManager
{
	/**
	 * Damage control objects
	 */
	public Map<Object, DamageControl> damage = new HashMap<Object, DamageControl>();

	public void reset()
	{
		this.damage.clear();
	}

	/**
	 * Start observing damage made to terrain
	 */
	public void addDamageControl(Object object, EntityLivingBase player)
	{
		if (Blockbuster.damageControl.get())
		{
			int dist = Blockbuster.damageControlDistance.get();

			this.damage.put(object, new DamageControl(player, dist));
		}
	}

	/**
	 * Restore made damage
	 */
	public void restoreDamageControl(Object object, World world)
	{
		DamageControl control = this.damage.remove(object);

		if (control != null)
		{
			control.apply(world);
		}
	}

	/**
	 * Add an entity to track
	 */
	public void addEntity(Entity entity)
	{
		for (DamageControl damage : this.damage.values())
		{
			damage.entities.add(entity);
		}
	}

	/**
	 * Add a block to track
	 */
	public void addBlock(BlockPos pos, IBlockState oldState, World worldIn)
	{
		for (DamageControl damage : CommonProxy.damage.damage.values())
		{
			damage.addBlock(new BlockPos(pos), oldState, worldIn);
		}
	}
}