package mchorse.blockbuster.client.particles.emitter;

import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.IComponentEmitterInitialize;
import mchorse.blockbuster.client.particles.components.IComponentEmitterUpdate;
import mchorse.blockbuster.client.particles.components.IComponentParticleInitialize;
import mchorse.blockbuster.client.particles.components.IComponentParticleRender;
import mchorse.blockbuster.client.particles.components.IComponentParticleUpdate;
import mchorse.mclib.math.Variable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BedrockEmitter
{
	public BedrockScheme scheme;
	public List<BedrockParticle> particles = new ArrayList<BedrockParticle>();

	public EntityLivingBase target;
	public World world;

	private BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

	/* Cached variable references to avoid hash look ups */
	private Variable random1;
	private Variable random2;
	private Variable random3;
	private Variable random4;

	public void setTarget(EntityLivingBase target)
	{
		this.target = target;
		this.world = target == null ? null : target.worldObj;
	}

	public void setWorld(World world)
	{
		this.world = world;
	}

	public void setScheme(BedrockScheme scheme)
	{
		this.scheme = scheme;

		if (this.scheme == null)
		{
			return;
		}

		for (IComponentEmitterInitialize component : this.scheme.getComponents(IComponentEmitterInitialize.class))
		{
			component.apply(this);
		}

		this.random1 = scheme.parser.variables.get("variable.particle_random_1");
		this.random2 = scheme.parser.variables.get("variable.particle_random_2");
		this.random3 = scheme.parser.variables.get("variable.particle_random_3");
		this.random4 = scheme.parser.variables.get("variable.particle_random_4");
	}

	private void setVariables(BedrockParticle particle)
	{
		if (this.random1 != null) this.random1.set(particle.random1);
		if (this.random2 != null) this.random2.set(particle.random2);
		if (this.random3 != null) this.random3.set(particle.random3);
		if (this.random4 != null) this.random4.set(particle.random4);
	}

	public void update()
	{
		this.spawnParticle();

		Iterator<BedrockParticle> it = this.particles.iterator();
		List<IComponentParticleUpdate> components = this.scheme.getComponents(IComponentParticleUpdate.class);

		for (IComponentEmitterUpdate component : this.scheme.getComponents(IComponentEmitterUpdate.class))
		{
			component.update(this);
		}

		while (it.hasNext())
		{
			BedrockParticle particle = it.next();

			particle.update();
			this.setVariables(particle);

			for (IComponentParticleUpdate component : components)
			{
				component.update(this, particle);
			}

			if (particle.dead)
			{
				it.remove();
			}
		}
	}

	private void spawnParticle()
	{
		BedrockParticle particle = new BedrockParticle();

		this.setVariables(particle);

		for (IComponentParticleInitialize component : this.scheme.getComponents(IComponentParticleInitialize.class))
		{
			component.apply(this, particle);
		}

		if (!particle.relative)
		{
			particle.x = particle.prevX = particle.x + (float) this.target.posX;
			particle.y = particle.prevY = particle.y + (float) this.target.posY;
			particle.z = particle.prevZ = particle.z + (float) this.target.posZ;
		}

		this.particles.add(particle);
	}

	public void render(float partialTicks)
	{
		if (this.particles.isEmpty())
		{
			return;
		}

		Minecraft.getMinecraft().getTextureManager().bindTexture(this.scheme.texture);
		List<IComponentParticleRender> renders = this.scheme.getComponents(IComponentParticleRender.class);
		VertexBuffer builder = Tessellator.getInstance().getBuffer();

		for (IComponentParticleRender component : renders)
		{
			component.preRender(this);
		}

		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

		for (BedrockParticle particle : this.particles)
		{
			for (IComponentParticleRender component : renders)
			{
				component.render(this, particle, builder, partialTicks);
			}
		}

		Tessellator.getInstance().draw();

		for (IComponentParticleRender component : renders)
		{
			component.postRender(this);
		}
	}

	public int getBrightnessForRender(float partialTicks, float x, float y, float z)
	{
		this.blockPos.setPos(x, y, z);

		return this.world.isBlockLoaded(this.blockPos) ? this.world.getCombinedLight(this.blockPos, 0) : 0;
	}
}