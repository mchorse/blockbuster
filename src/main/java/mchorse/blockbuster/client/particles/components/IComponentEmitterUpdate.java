package mchorse.blockbuster.client.particles.components;

import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;

public interface IComponentEmitterUpdate extends IComponentBase
{
	public void update(BedrockEmitter emitter);
}