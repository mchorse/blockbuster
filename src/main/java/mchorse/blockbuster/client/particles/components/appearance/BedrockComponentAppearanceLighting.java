package mchorse.blockbuster.client.particles.components.appearance;

import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentEmitterInitialize;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;

public class BedrockComponentAppearanceLighting extends BedrockComponentBase implements IComponentEmitterInitialize
{
	@Override
	public void apply(BedrockEmitter emitter)
	{
		emitter.lit = false;
	}

	@Override
	public boolean canBeEmpty()
	{
		return true;
	}
}