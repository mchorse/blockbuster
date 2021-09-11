package mchorse.blockbuster.client.particles.components.appearance;

import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentParticleRender;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.metamorph.api.Morph;
import net.minecraft.client.renderer.BufferBuilder;

public class BedrockComponentMorph extends BedrockComponentBase implements IComponentParticleRender
{
    public Morph morph = new Morph();
    public boolean enabled = false;

    @Override
    public void preRender(BedrockEmitter emitter, float partialTicks)
    {

    }

    @Override
    public void render(BedrockEmitter emitter, BedrockParticle particle, BufferBuilder builder, float partialTicks)
    {

    }

    @Override
    public void renderOnScreen(BedrockParticle particle, int x, int y, float scale, float partialTicks)
    {

    }

    @Override
    public void postRender(BedrockEmitter emitter, float partialTicks)
    {

    }
}
