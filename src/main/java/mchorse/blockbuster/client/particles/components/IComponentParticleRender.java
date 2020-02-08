package mchorse.blockbuster.client.particles.components;

import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import net.minecraft.client.renderer.BufferBuilder;

public interface IComponentParticleRender extends IComponentBase
{
	public void preRender(BedrockEmitter emitter, float partialTicks);

	public void render(BedrockEmitter emitter, BedrockParticle particle, BufferBuilder builder, float partialTicks);

	public void renderOnScreen(BedrockParticle particle, int x, int y, float scale, float partialTicks);

	public void postRender(BedrockEmitter emitter, float partialTicks);
}