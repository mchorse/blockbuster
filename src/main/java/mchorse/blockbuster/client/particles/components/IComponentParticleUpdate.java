package mchorse.blockbuster.client.particles.components;

import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;

public interface IComponentParticleUpdate
{
	public void update(BedrockEmitter emitter, BedrockParticle particle);
}