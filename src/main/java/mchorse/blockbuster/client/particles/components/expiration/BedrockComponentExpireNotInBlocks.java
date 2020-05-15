package mchorse.blockbuster.client.particles.components.expiration;

import mchorse.blockbuster.client.particles.components.IComponentParticleUpdate;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import net.minecraft.block.Block;

public class BedrockComponentExpireNotInBlocks extends BedrockComponentExpireBlocks implements IComponentParticleUpdate
{
	@Override
	public void update(BedrockEmitter emitter, BedrockParticle particle)
	{
		if (particle.dead || emitter.world == null)
		{
			return;
		}

		Block current = this.getBlock(emitter, particle);

		for (Block block : this.blocks)
		{
			if (block == current)
			{
				return;
			}
		}

		particle.dead = true;
	}
}