package mchorse.blockbuster_pack.utils;

import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.blockbuster_pack.morphs.ISyncableMorph;
import mchorse.blockbuster_pack.morphs.ImageMorph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.models.IMorphProvider;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.bodypart.BodyPartManager;
import mchorse.metamorph.bodypart.IBodyPartProvider;
import net.minecraft.nbt.NBTTagCompound;

public class PausedMorph
{
	public int offset = -1;
	public AbstractMorph previous;

	public boolean isPaused()
	{
		return this.offset >= 0;
	}

	public void copy(PausedMorph pause)
	{
		this.offset = pause.offset;
		this.previous = pause.previous;
	}

	public void set(AbstractMorph previous, int offset)
	{
		this.offset = offset;
		this.previous = previous;
	}

	public void reset()
	{
		this.offset = -1;
		this.previous = null;
	}

	public void toNBT(NBTTagCompound tag)
	{
		if (this.isPaused())
		{
			tag.setInteger("Pause", this.offset);

			if (this.previous != null)
			{
				tag.setTag("PausePrevious", this.previous.toNBT());
			}
		}
	}

	public void fromNBT(NBTTagCompound tag)
	{
		this.reset();

		if (tag.hasKey("Pause"))
		{
			this.offset = tag.getInteger("Pause");

			if (tag.hasKey("PausePrevious"))
			{
				this.previous = MorphManager.INSTANCE.morphFromNBT(tag.getCompoundTag("PausePrevious"));
			}
		}
	}

	public void applyAnimation(Animation animation)
	{
		if (this.isPaused())
		{
			animation.pause();
			animation.progress = this.offset;
		}
	}

	public void applyAnimation(CustomMorph.PoseAnimation animation)
	{
		if (this.isPaused())
		{
			animation.pause();
			animation.progress = this.offset;

			AbstractMorph previous = this.previous;

			if (previous instanceof IMorphProvider)
			{
				previous = ((IMorphProvider) previous).getMorph();
			}

			if (previous instanceof CustomMorph)
			{
				animation.last = ((CustomMorph) previous).getCurrentPose();
			}
		}
	}

	public void applyAnimation(ImageMorph.ImageAnimation animation)
	{
		if (this.isPaused())
		{
			animation.pause();
			animation.progress = this.offset;

			if (this.previous instanceof ImageMorph)
			{
				animation.last = new ImageMorph.ImageProperties();
				animation.last.from((ImageMorph) this.previous);
			}
		}
	}

	public void recursivePausing(AbstractMorph morph)
	{
		this.recursivePausing(morph, this.previous, this.offset);
	}

	public void recursivePausing(AbstractMorph morph, AbstractMorph previous, int offset)
	{
		if (!(morph instanceof IBodyPartProvider && previous instanceof IBodyPartProvider))
		{
			return;
		}

		BodyPartManager main = ((IBodyPartProvider) morph).getBodyPart();
		BodyPartManager secondary = ((IBodyPartProvider) previous).getBodyPart();

		for (int i = 0, c = main.parts.size(); i < c; i++)
		{
			if (i >= secondary.parts.size())
			{
				break;
			}

			AbstractMorph mainMorph = main.parts.get(i).morph.get();
			AbstractMorph secondaryMorph = secondary.parts.get(i).morph.get();

			if (mainMorph instanceof ISyncableMorph && secondaryMorph != null)
			{
				((ISyncableMorph) mainMorph).pauseMorph(secondaryMorph, offset);
			}
			else if (mainMorph instanceof IBodyPartProvider)
			{
				this.recursivePausing(mainMorph, secondaryMorph, offset);
			}
		}
	}
}