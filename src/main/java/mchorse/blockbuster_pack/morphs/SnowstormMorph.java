package mchorse.blockbuster_pack.morphs;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.RenderingHandler;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

public class SnowstormMorph extends AbstractMorph
{
	public String scheme = "";

	@SideOnly(Side.CLIENT)
	public BedrockEmitter emitter = new BedrockEmitter();

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
	public void renderOnScreen(EntityPlayer entityPlayer, int i, int i1, float v, float v1)
	{}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(EntityLivingBase entityLivingBase, double v, double v1, double v2, float v3, float v4)
	{
		this.emitter.setTarget(entityLivingBase);
		RenderingHandler.emitters.add(this.emitter);
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
		this.emitter.setTarget(target);
		this.emitter.update();
	}

	@Override
	public AbstractMorph clone(boolean b)
	{
		SnowstormMorph morph = new SnowstormMorph();

		morph.name = this.name;
		morph.settings = this.settings;
		morph.scheme = this.scheme;

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
	}

	@Override
	public void toNBT(NBTTagCompound tag)
	{
		super.toNBT(tag);

		tag.setString("Scheme", this.scheme);
	}
}