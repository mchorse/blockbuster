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

import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BedrockEmitter
{
	public BedrockScheme scheme;
	public List<BedrockParticle> particles = new ArrayList<BedrockParticle>();

	public EntityLivingBase target;
	public World world;
	public boolean lit;

	/* Runtime properties */
	private int age;
	private int lifetime;
	public boolean playing = true;

	public float random1 = (float) Math.random();
	public float random2 = (float) Math.random();
	public float random3 = (float) Math.random();
	public float random4 = (float) Math.random();
	public double spawnedParticles;

	private BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

	/* Cached variable references to avoid hash look ups */
	private Variable varAge;
	private Variable varLifetime;
	private Variable varRandom1;
	private Variable varRandom2;
	private Variable varRandom3;
	private Variable varRandom4;

	private Variable varEmitterAge;
	private Variable varEmitterLifetime;
	private Variable varEmitterRandom1;
	private Variable varEmitterRandom2;
	private Variable varEmitterRandom3;
	private Variable varEmitterRandom4;

	public double getAge()
	{
		return this.getAge(0);
	}

	public double getAge(float partialTicks)
	{
		return (this.age + partialTicks) / 20.0;
	}

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

		this.lit = true;
		this.stop();
		this.start();

		this.setupVariables();
		this.setEmitterVariables(0);

		for (IComponentEmitterInitialize component : this.scheme.getComponents(IComponentEmitterInitialize.class))
		{
			component.apply(this);
		}
	}

	private void setupVariables()
	{
		this.varAge = scheme.parser.variables.get("variable.particle_age");
		this.varLifetime = scheme.parser.variables.get("variable.particle_lifetime");
		this.varRandom1 = scheme.parser.variables.get("variable.particle_random_1");
		this.varRandom2 = scheme.parser.variables.get("variable.particle_random_2");
		this.varRandom3 = scheme.parser.variables.get("variable.particle_random_3");
		this.varRandom4 = scheme.parser.variables.get("variable.particle_random_4");

		this.varEmitterAge = scheme.parser.variables.get("variable.emitter_age");
		this.varEmitterLifetime = scheme.parser.variables.get("variable.emitter_lifetime");
		this.varEmitterRandom1 = scheme.parser.variables.get("variable.emitter_random_1");
		this.varEmitterRandom2 = scheme.parser.variables.get("variable.emitter_random_2");
		this.varEmitterRandom3 = scheme.parser.variables.get("variable.emitter_random_3");
		this.varEmitterRandom4 = scheme.parser.variables.get("variable.emitter_random_4");
	}

	public void setParticleVariables(BedrockParticle particle, float partialTicks)
	{
		if (this.varAge != null) this.varAge.set(particle.getAge(partialTicks));
		if (this.varLifetime != null) this.varLifetime.set(particle.lifetime / 20.0);
		if (this.varRandom1 != null) this.varRandom1.set(particle.random1);
		if (this.varRandom2 != null) this.varRandom2.set(particle.random2);
		if (this.varRandom3 != null) this.varRandom3.set(particle.random3);
		if (this.varRandom4 != null) this.varRandom4.set(particle.random4);
	}

	public void setEmitterVariables(float partialTicks)
	{
		if (this.varEmitterAge != null) this.varEmitterAge.set(this.getAge(partialTicks));
		if (this.varEmitterLifetime != null) this.varEmitterLifetime.set(this.lifetime / 20.0);
		if (this.varEmitterRandom1 != null) this.varEmitterRandom1.set(this.random1);
		if (this.varEmitterRandom2 != null) this.varEmitterRandom2.set(this.random2);
		if (this.varEmitterRandom3 != null) this.varEmitterRandom3.set(this.random3);
		if (this.varEmitterRandom4 != null) this.varEmitterRandom4.set(this.random4);
	}

	public void start()
	{
		if (this.playing)
		{
			return;
		}

		this.age = 0;
		this.spawnedParticles = 0;
		this.playing = true;
	}

	public void stop()
	{
		if (!this.playing)
		{
			return;
		}

		this.age = 0;
		this.spawnedParticles = 0;
		this.playing = false;
	}

	public void update()
	{
		if (this.scheme == null)
		{
			return;
		}

		this.setEmitterVariables(0);

		for (IComponentEmitterUpdate component : this.scheme.getComponents(IComponentEmitterUpdate.class))
		{
			component.update(this);
		}

		this.setEmitterVariables(0);
		this.updateParticles();
		this.age++;
	}

	private void updateParticles()
	{
		List<IComponentParticleUpdate> components = this.scheme.getComponents(IComponentParticleUpdate.class);
		Iterator<BedrockParticle> it = this.particles.iterator();

		while (it.hasNext())
		{
			BedrockParticle particle = it.next();

			particle.update();

			this.setParticleVariables(particle, 0);

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

	public void spawnParticle()
	{
		BedrockParticle particle = new BedrockParticle();

		this.setParticleVariables(particle, 0);

		for (IComponentParticleInitialize component : this.scheme.getComponents(IComponentParticleInitialize.class))
		{
			component.apply(this, particle);
		}

		if (!particle.relative)
		{
			particle.position.add(new Vector3d(this.target.posX, this.target.posY, this.target.posZ));
		}

		particle.prevPosition.set(particle.position);
		particle.prevRotation = particle.rotation;

		this.particles.add(particle);
	}

	public void render(float partialTicks)
	{
		if (this.scheme == null)
		{
			return;
		}

		List<IComponentParticleRender> renders = this.scheme.getComponents(IComponentParticleRender.class);
		VertexBuffer builder = Tessellator.getInstance().getBuffer();

		for (IComponentParticleRender component : renders)
		{
			component.preRender(this, partialTicks);
		}

		if (!this.particles.isEmpty())
		{
			Minecraft.getMinecraft().getTextureManager().bindTexture(this.scheme.texture);
			builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

			for (BedrockParticle particle : this.particles)
			{
				this.setEmitterVariables(partialTicks);
				this.setParticleVariables(particle, partialTicks);

				for (IComponentParticleRender component : renders)
				{
					component.render(this, particle, builder, partialTicks);
				}
			}

			Tessellator.getInstance().draw();
		}

		for (IComponentParticleRender component : renders)
		{
			component.postRender(this, partialTicks);
		}
	}

	public int getBrightnessForRender(float partialTicks, double x, double y, double z)
	{
		if (this.lit)
		{
			return 15728880;
		}

		this.blockPos.setPos(x, y, z);

		return this.world.isBlockLoaded(this.blockPos) ? this.world.getCombinedLight(this.blockPos, 0) : 0;
	}
}