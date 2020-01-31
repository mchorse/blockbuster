package mchorse.blockbuster.client.particles.emitter;

import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.IComponentEmitterInitialize;
import mchorse.blockbuster.client.particles.components.IComponentParticleInitialize;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix3f;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BedrockEmitter
{
	public BedrockScheme scheme;
	public List<BedrockParticle> particles = new ArrayList<BedrockParticle>();

	public boolean lighting;

	public void setScheme(BedrockScheme scheme)
	{
		this.scheme = scheme;
		this.lighting = false;

		for (IComponentEmitterInitialize component : this.scheme.getComponents(IComponentEmitterInitialize.class))
		{

		}
	}

	public void update()
	{
		/* TODO: spawn more particles */

		Iterator<BedrockParticle> it = this.particles.iterator();

		while (it.hasNext())
		{
			BedrockParticle particle = it.next();

			particle.update();

			if (particle.dead)
			{
				it.remove();
			}
		}
	}

	private void spawnParticle()
	{
		BedrockParticle particle = new BedrockParticle();

		for (IComponentParticleInitialize component : this.scheme.getComponents(IComponentParticleInitialize.class))
		{
			component.apply(particle, this);
		}

		this.particles.add(particle);
	}

	public void render()
	{
		this.render(0, 0, 0);
	}

	public void render(double x, double y, double z)
	{
		if (this.particles.isEmpty())
		{
			return;
		}

		if (this.lighting) GlStateManager.enableLighting();

		BufferBuilder builder = Tessellator.getInstance().getBuffer();

		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

		for (BedrockParticle particle : this.particles)
		{
			/* TODO: render particle */
			builder.pos(0, 0, 0).tex(0, 0).color(particle.r, particle.g, particle.b, particle.a).lightmap(0, 0).endVertex();
		}

		Tessellator.getInstance().draw();

		if (this.lighting) GlStateManager.disableLighting();
	}
}