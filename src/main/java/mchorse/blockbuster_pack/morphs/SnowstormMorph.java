package mchorse.blockbuster_pack.morphs;

import mchorse.blockbuster.client.RenderingHandler;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SnowstormMorph extends AbstractMorph
{
	public String scheme = "";

	public boolean renderInside;

	@SideOnly(Side.CLIENT)
	private BedrockEmitter emitter = new BedrockEmitter();

	public SnowstormMorph()
	{
		super();
		this.name = "snowstorm";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderOnScreen(EntityPlayer entityPlayer, int i, int i1, float v, float v1)
	{}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(EntityLivingBase entityLivingBase, double v, double v1, double v2, float v3, float v4)
	{
		if (this.renderInside)
		{
			/* TODO: render emitter */
		}
		else
		{
			RenderingHandler.emitters.add(null);
		}
	}

	@Override
	public void update(EntityLivingBase target, IMorphing cap)
	{
		super.update(target, cap);

		/* TODO: update emitter */
	}

	@Override
	public AbstractMorph clone(boolean b)
	{
		SnowstormMorph morph = new SnowstormMorph();



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
}