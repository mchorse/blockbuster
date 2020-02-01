package mchorse.blockbuster.client.particles.components;

import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import net.minecraft.client.renderer.VertexBuffer;

public interface IComponentParticleRender
{
	public void preRender(BedrockEmitter emitter);

	public void render(BedrockEmitter emitter, BedrockParticle particle, VertexBuffer builder, float partialTicks);

	public void postRender(BedrockEmitter emitter);
}