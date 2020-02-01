package mchorse.blockbuster.client.particles.components.shape;

import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;

public class BedrockComponentShapePoint extends BedrockComponentShapeBase
{
	@Override
	public void apply(BedrockEmitter emitter, BedrockParticle particle)
	{
		particle.x = particle.prevX = this.offset[0].evaluate();
		particle.y = particle.prevY = this.offset[1].evaluate();
		particle.z = particle.prevZ = this.offset[2].evaluate();

		this.direction.applyDirection(particle, particle.x, particle.y, particle.z);
	}
}