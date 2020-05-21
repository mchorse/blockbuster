package mchorse.blockbuster_pack.morphs;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.RenderingHandler;
import mchorse.blockbuster.client.particles.BedrockLibrary;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.utils.Interpolations;
import mchorse.mclib.utils.MatrixUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class SnowstormMorph extends AbstractMorph
{
	@SideOnly(Side.CLIENT)
	private static Matrix4f matrix;

	@SideOnly(Side.CLIENT)
	private static Vector4f vector;

	public String scheme = "";

	private BedrockEmitter emitter;
	public List<BedrockEmitter> lastEmitters;

	private boolean initialized;
	private long lastUpdate;

	public static Matrix4f getMatrix()
	{
		if (matrix == null)
		{
			matrix = new Matrix4f();
		}

		return matrix;
	}

	public static Vector4f getVector()
	{
		if (vector == null)
		{
			vector = new Vector4f();
		}

		return vector;
	}

	@SideOnly(Side.CLIENT)
	public static Vector4f calculateGlobal(Matrix4f matrix, EntityLivingBase entity, float x, float y, float z, float partial)
	{
		Vector4f vector4f = getVector();

		vector4f.set(x, y, z, 1);
		matrix.transform(vector4f);
		vector4f.add(new Vector4f(
			(float) Interpolations.lerp(entity.prevPosX, entity.posX, partial),
			(float) Interpolations.lerp(entity.prevPosY, entity.posY, partial),
			(float) Interpolations.lerp(entity.prevPosZ, entity.posZ, partial),
			(float) 0
		));

		return vector4f;
	}

	public SnowstormMorph()
	{
		super();
		this.name = "snowstorm";
	}

	public BedrockEmitter getEmitter()
	{
		if (this.emitter == null)
		{
			this.emitter = new BedrockEmitter();
			this.setClientScheme(this.scheme);
		}

		return this.emitter;
	}

	public List<BedrockEmitter> getLastEmitters()
	{
		if (this.lastEmitters == null)
		{
			this.lastEmitters = new ArrayList<BedrockEmitter>();
		}

		return this.lastEmitters;
	}

	public void setScheme(String key)
	{
		this.scheme = key;
		this.initialized = false;

		if (this.emitter != null)
		{
			this.setClientScheme(key);
		}
	}

	private void setClientScheme(String key)
	{
		this.getEmitter().running = false;
		this.getLastEmitters().add(this.getEmitter());

		this.emitter = new BedrockEmitter();
		this.emitter.setScheme(this.getScheme(key));
	}

	private BedrockScheme getScheme(String key)
	{
		return Blockbuster.proxy.particles.presets.get(key);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getSubclassDisplayName()
	{
		return this.getEmitter().scheme != null ? this.getEmitter().scheme.identifier : this.name;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderOnScreen(EntityPlayer entityPlayer, int x, int y, float scale, float alpha)
	{
		this.getEmitter().renderOnScreen(x, y, scale);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(EntityLivingBase entityLivingBase, double x, double y, double z, float yaw, float partialTicks)
	{
		if (GuiModelRenderer.isRendering())
		{
			return;
		}

		if (this.emitter != null && this.emitter.scheme != null && this.lastUpdate < BedrockLibrary.lastUpdate)
		{
			this.lastUpdate = BedrockLibrary.lastUpdate;

			if (this.emitter.scheme != this.getScheme(this.scheme))
			{
				this.setClientScheme(this.scheme);
			}
		}

		BedrockEmitter emitter = this.getEmitter();

		if (MatrixUtils.matrix != null)
		{
			Matrix4f parent = new Matrix4f(MatrixUtils.matrix);
			Matrix4f matrix4f = MatrixUtils.readModelView(getMatrix());

			parent.invert();
			parent.mul(matrix4f);

			Vector4f zero = calculateGlobal(parent, entityLivingBase, 0, 0, 0, partialTicks);

			emitter.lastGlobal.x = zero.x;
			emitter.lastGlobal.y = zero.y;
			emitter.lastGlobal.z = zero.z;
			emitter.rotation.setIdentity();

			Vector3f ax = new Vector3f(parent.m00, parent.m01, parent.m02);
			Vector3f ay = new Vector3f(parent.m10, parent.m11, parent.m12);
			Vector3f az = new Vector3f(parent.m20, parent.m21, parent.m22);

			ax.normalize();
			ay.normalize();
			az.normalize();

			emitter.rotation.setRow(0, ax);
			emitter.rotation.setRow(1, ay);
			emitter.rotation.setRow(2, az);

			for (BedrockEmitter last : this.getLastEmitters())
			{
				last.lastGlobal.set(emitter.lastGlobal);
				last.rotation.set(emitter.rotation);
			}

			this.initialized = true;
		}
		else
		{
			emitter.lastGlobal.x = Interpolations.lerp(entityLivingBase.prevPosX, entityLivingBase.posX, partialTicks);
			emitter.lastGlobal.y = Interpolations.lerp(entityLivingBase.prevPosY, entityLivingBase.posY, partialTicks);
			emitter.lastGlobal.z = Interpolations.lerp(entityLivingBase.prevPosZ, entityLivingBase.posZ, partialTicks);
			emitter.rotation.setIdentity();

			for (BedrockEmitter last : this.getLastEmitters())
			{
				last.lastGlobal.set(emitter.lastGlobal);
				last.rotation.set(emitter.rotation);
			}

			this.initialized = true;
		}

		if (this.initialized)
		{
			this.setupEmitter(emitter, entityLivingBase);
			RenderingHandler.addEmitter(emitter);

			for (BedrockEmitter last : this.getLastEmitters())
			{
				this.setupEmitter(last, entityLivingBase);
				RenderingHandler.addEmitter(last);
			}
		}
	}

	@Override
	public void update(EntityLivingBase target)
	{
		super.update(target);

		if (target.world.isRemote && this.initialized)
		{
			this.updateEmitter(target);
		}
	}

	@SideOnly(Side.CLIENT)
	private void updateEmitter(EntityLivingBase target)
	{
		this.setupEmitter(this.getEmitter(), target);
		this.getEmitter().update();

		Iterator<BedrockEmitter> it = this.getLastEmitters().iterator();

		while (it.hasNext())
		{
			BedrockEmitter last = it.next();

			this.setupEmitter(last, target);
			last.update();

			if (last.isFinished())
			{
				it.remove();
			}
		}
	}

	private void setupEmitter(BedrockEmitter emitter, EntityLivingBase target)
	{
		emitter.setTarget(target);
	}

	@Override
	public AbstractMorph create()
	{
		return new SnowstormMorph();
	}

	@Override
	public void copy(AbstractMorph from)
	{
		super.copy(from);

		if (from instanceof SnowstormMorph)
		{
			SnowstormMorph morph = (SnowstormMorph) from;

			this.setScheme(morph.scheme);
		}
	}

	@Override
	public float getWidth(EntityLivingBase entityLivingBase)
	{
		return 0.6F;
	}

	@Override
	public float getHeight(EntityLivingBase entityLivingBase)
	{
		return 1.8F;
	}

	@Override
	public boolean equals(Object obj)
	{
		boolean result = super.equals(obj);

		if (obj instanceof SnowstormMorph)
		{
			SnowstormMorph morph = (SnowstormMorph) obj;

			result = result && Objects.equals(this.scheme, morph.scheme);
		}

		return result;
	}

	@Override
	public boolean canMerge(AbstractMorph morph)
	{
		if (morph instanceof SnowstormMorph)
		{
			SnowstormMorph snow = (SnowstormMorph) morph;

			if (!this.scheme.equals(snow.scheme) && this.emitter != null)
			{
				this.setScheme(snow.scheme);
			}

			return true;
		}

		return super.canMerge(morph);
	}

	@Override
	public void reset()
	{
		super.reset();

		this.scheme = "";
		this.initialized = false;
	}

	@Override
	public void fromNBT(NBTTagCompound tag)
	{
		super.fromNBT(tag);

		if (tag.hasKey("Scheme"))
		{
			this.setScheme(tag.getString("Scheme"));
		}
	}

	@Override
	public void toNBT(NBTTagCompound tag)
	{
		super.toNBT(tag);

		tag.setString("Scheme", this.scheme);
	}
}