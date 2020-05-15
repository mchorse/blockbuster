package mchorse.blockbuster.client.particles.components.lifetime;

import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;

public class BedrockComponentLifetimeOnce extends BedrockComponentLifetime
{
	@Override
	public void update(BedrockEmitter emitter)
	{
		if (emitter.getAge() >= this.activeTime.get())
		{
			emitter.stop();
		}
	}
}