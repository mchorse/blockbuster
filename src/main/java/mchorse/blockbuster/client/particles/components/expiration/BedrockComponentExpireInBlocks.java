package mchorse.blockbuster.client.particles.components.expiration;

import mchorse.blockbuster.client.particles.components.IComponentParticleUpdate;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import net.minecraft.block.Block;

public class BedrockComponentExpireInBlocks extends BedrockComponentExpireBlocks implements IComponentParticleUpdate
{
	@Override
	public void update(BedrockEmitter emitter, BedrockParticle particle)
	{
		if (particle.dead)
		{
			return;
		}

		Block current = this.getBlock(emitter, particle);

		for (Block block : this.blocks)
		{
			if (block == current)
			{
				particle.dead = true;

				return;
			}
		}
	}
}