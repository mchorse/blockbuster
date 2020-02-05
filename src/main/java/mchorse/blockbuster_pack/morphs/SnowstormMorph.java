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

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;
import java.util.Objects;

public class SnowstormMorph extends AbstractMorph
{
	public static final Matrix4f matrix = new Matrix4f();

	public String scheme = "";

	@SideOnly(Side.CLIENT)
	public BedrockEmitter emitter = new BedrockEmitter();

	public boolean local;

	public SnowstormMorph()
	{
		super();
		this.name = "snowstorm";
	}

	public void setScheme(String key)
	{
		this.scheme = key;
		this.emitter.setScheme(Blockbuster.proxy.particles.presets.get(key));
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
		if (RenderCustomModel.matrix != null)
		{
			Matrix4f parent = new Matrix4f(RenderCustomModel.matrix);
			Matrix4f matrix4f = MatrixUtils.readModelView(matrix);
			Vector4f zero = new Vector4f(0, 0, 0, 1);

			parent.invert();
			parent.mul(matrix4f);
			parent.transform(zero);

			zero.add(new Vector4f(
				(float) Interpolations.lerp(entityLivingBase.prevPosX, entityLivingBase.posX, partialTicks),
				(float) Interpolations.lerp(entityLivingBase.prevPosY, entityLivingBase.posY, partialTicks),
				(float) Interpolations.lerp(entityLivingBase.prevPosZ, entityLivingBase.posZ, partialTicks),
				(float) 0
			));

			this.emitter.lastGlobal.x = zero.x;
			this.emitter.lastGlobal.y = zero.y;
			this.emitter.lastGlobal.z = zero.z;
		}
		else
		{
			this.emitter.lastGlobal.x = Interpolations.lerp(entityLivingBase.prevPosX, entityLivingBase.posX, partialTicks);
			this.emitter.lastGlobal.y = Interpolations.lerp(entityLivingBase.prevPosY, entityLivingBase.posY, partialTicks);
			this.emitter.lastGlobal.z = Interpolations.lerp(entityLivingBase.prevPosZ, entityLivingBase.posZ, partialTicks);
		}

		this.setupEmitter(entityLivingBase);
		RenderingHandler.addEmitter(this.emitter);
	}

	@Override
	public void update(EntityLivingBase target, IMorphing cap)
	{
		super.update(target, cap);

		if (target.worldObj.isRemote)
		{
			this.updateEmitter(target);
		}
	}

	@SideOnly(Side.CLIENT)
	private void updateEmitter(EntityLivingBase target)
	{
		this.setupEmitter(target);
		this.emitter.update();
	}

	private void setupEmitter(EntityLivingBase target)
	{
		this.emitter.setTarget(target);
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
	public void reset()
	{
		super.reset();

		this.scheme = "";
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