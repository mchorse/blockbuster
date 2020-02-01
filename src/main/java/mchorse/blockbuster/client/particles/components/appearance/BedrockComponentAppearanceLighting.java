package mchorse.blockbuster.client.particles.components.appearance;

import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentParticleRender;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VertexBuffer;

public class BedrockComponentAppearanceLighting extends BedrockComponentBase implements IComponentParticleRender
{
	@Override
	public void preRender(BedrockEmitter emitter)
	{
		GlStateManager.enableLighting();
	}

	@Override
	public void render(BedrockEmitter emitter, BedrockParticle particle, VertexBuffer builder, float partialTicks)
	{}

	@Override
	public void postRender(BedrockEmitter emitter)
	{
		GlStateManager.disableLighting();
	}
}