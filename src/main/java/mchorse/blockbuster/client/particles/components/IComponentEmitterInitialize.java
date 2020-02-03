package mchorse.blockbuster.client.particles.components;

import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;

public interface IComponentEmitterInitialize extends IComponentBase
{
	public void apply(BedrockEmitter emitter);
}