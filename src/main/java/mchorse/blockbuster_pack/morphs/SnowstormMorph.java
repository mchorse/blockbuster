package mchorse.blockbuster_pack.morphs;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.RenderingHandler;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.render.RenderCustomModel;
import mchorse.blockbuster.utils.MatrixUtils;
import mchorse.mclib.utils.Interpolations;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class SnowstormMorph extends AbstractMorph
{
	public static final Matrix4f matrix = new Matrix4f();
	public static final Vector4f vector = new Vector4f();

	public String scheme = "";

	@SideOnly(Side.CLIENT)
	public BedrockEmitter emitter = new BedrockEmitter();

	@SideOnly(Side.CLIENT)
	public List<BedrockEmitter> lastEmitters = new ArrayList<BedrockEmitter>();

	public boolean local;

	private boolean initialized;

	public static Vector4f calculateGlobal(Matrix4f matrix, EntityLivingBase entity, float x, float y, float z, float partial)
	{
		vector.set(x, y, z, 1);

		matrix.transform(vector);

		vector.add(new Vector4f(
			(float) Interpolations.lerp(entity.prevPosX, entity.posX, partial),
			(float) Interpolations.lerp(entity.prevPosY, entity.posY, partial),
			(float) Interpolations.lerp(entity.prevPosZ, entity.posZ, partial),
			(float) 0
		));

		return vector;
	}

	public SnowstormMorph()
	{
		super();
		this.name = "snowstorm";
	}

	public void setScheme(String key)
	{
		this.scheme = key;
		this.emitter.setScheme(Blockbuster.proxy.particles.presets.get(key));
		this.initialized = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderOnScreen(EntityPlayer entityPlayer, int x, int y, float scale, float alpha)
	{
		this.emitter.renderOnScreen(x, y, scale);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(EntityLivingBase entityLivingBase, double x, double y, double z, float yaw, float partialTicks)
	{
		if (MatrixUtils.matrix != null)
		{
			Matrix4f parent = new Matrix4f(MatrixUtils.matrix);
			Matrix4f matrix4f = MatrixUtils.readModelView(matrix);

			parent.invert();
			parent.mul(matrix4f);

			Vector4f zero = calculateGlobal(parent, entityLivingBase, 0, 0, 0, partialTicks);

			this.emitter.lastGlobal.x = zero.x;
			this.emitter.lastGlobal.y = zero.y;
			this.emitter.lastGlobal.z = zero.z;
			this.emitter.rotation.setIdentity();

			Vector3f ax = new Vector3f(parent.m00, parent.m01, parent.m02);
			Vector3f ay = new Vector3f(parent.m10, parent.m11, parent.m12);
			Vector3f az = new Vector3f(parent.m20, parent.m21, parent.m22);

			ax.normalize();
			ay.normalize();
			az.normalize();

			this.emitter.rotation.setRow(0, ax);
			this.emitter.rotation.setRow(1, ay);
			this.emitter.rotation.setRow(2, az);

			for (BedrockEmitter last : this.lastEmitters)
			{
				last.lastGlobal.set(this.emitter.lastGlobal);
				last.rotation.set(this.emitter.rotation);
			}

			this.initialized = true;
		}
		else
		{
			this.emitter.lastGlobal.x = Interpolations.lerp(entityLivingBase.prevPosX, entityLivingBase.posX, partialTicks);
			this.emitter.lastGlobal.y = Interpolations.lerp(entityLivingBase.prevPosY, entityLivingBase.posY, partialTicks);
			this.emitter.lastGlobal.z = Interpolations.lerp(entityLivingBase.prevPosZ, entityLivingBase.posZ, partialTicks);
			this.emitter.rotation.setIdentity();

			for (BedrockEmitter last : this.lastEmitters)
			{
				last.lastGlobal.set(this.emitter.lastGlobal);
				last.rotation.set(this.emitter.rotation);
			}

			this.initialized = true;
		}

		if (this.initialized)
		{
			this.setupEmitter(this.emitter, entityLivingBase);
			RenderingHandler.addEmitter(this.emitter);

			for (BedrockEmitter last : this.lastEmitters)
			{
				this.setupEmitter(last, entityLivingBase);
				RenderingHandler.addEmitter(last);
			}
		}
	}

	@Override
	public void update(EntityLivingBase target, IMorphing cap)
	{
		super.update(target, cap);

		if (target.worldObj.isRemote && this.initialized)
		{
			this.updateEmitter(target);
		}
	}

	@SideOnly(Side.CLIENT)
	private void updateEmitter(EntityLivingBase target)
	{
		this.setupEmitter(this.emitter, target);
		this.emitter.update();

		Iterator<BedrockEmitter> it = this.lastEmitters.iterator();

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
	public AbstractMorph clone(boolean b)
	{
		SnowstormMorph morph = new SnowstormMorph();

		morph.name = this.name;
		morph.settings = this.settings;
		morph.scheme = this.scheme;
		morph.local = this.local;
		morph.setScheme(morph.scheme);

		return morph;
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
			result = result && this.local == morph.local;
		}

		return result;
	}

	@Override
	public boolean canMerge(AbstractMorph morph, boolean isRemote)
	{
		if (morph instanceof SnowstormMorph)
		{
			SnowstormMorph snow = (SnowstormMorph) morph;

			this.emitter.running = false;
			this.lastEmitters.add(this.emitter);

			this.emitter = new BedrockEmitter();
			this.setScheme(snow.scheme);

			return true;
		}

		return super.canMerge(morph, isRemote);
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

		if (tag.hasKey("Local"))
		{
			this.local = tag.getBoolean("Local");
		}
	}

	@Override
	public void toNBT(NBTTagCompound tag)
	{
		super.toNBT(tag);

		tag.setString("Scheme", this.scheme);
		tag.setBoolean("Local", this.local);
	}
}