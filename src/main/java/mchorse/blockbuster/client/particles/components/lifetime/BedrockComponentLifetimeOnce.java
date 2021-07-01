package mchorse.blockbuster.client.particles.components.lifetime;

import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;

public class BedrockComponentLifetimeOnce extends BedrockComponentLifetime
{
    @Override
    public void update(BedrockEmitter emitter)
    {
        double time = this.activeTime.get();

        emitter.lifetime = (int) (time * 20);

        if (emitter.getAge() >= time)
        {
            emitter.stop();
        }
    }
}