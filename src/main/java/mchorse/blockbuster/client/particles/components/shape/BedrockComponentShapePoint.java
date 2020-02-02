package mchorse.blockbuster.client.particles.components.shape;

import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;

public class BedrockComponentShapePoint extends BedrockComponentShapeBase
{
	@Override
	public void apply(BedrockEmitter emitter, BedrockParticle particle)
	{
		particle.x = particle.prevX = (float) this.offset[0].get();
		particle.y = particle.prevY = (float) this.offset[1].get();
		particle.z = particle.prevZ = (float) this.offset[2].get();

		this.direction.applyDirection(particle, particle.x, particle.y, particle.z);
	}
}