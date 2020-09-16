package mchorse.blockbuster_pack.utils;

import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.blockbuster_pack.morphs.ISyncableMorph;
import mchorse.blockbuster_pack.morphs.ImageMorph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.bodypart.BodyPart;
import mchorse.metamorph.bodypart.BodyPartManager;
import mchorse.metamorph.bodypart.IBodyPartProvider;
import net.minecraft.nbt.NBTTagCompound;

public class PausedMorph <T extends AbstractMorph>
{
	public int offset = -1;
	public T previous;
	public Class<T> clazz;

	public boolean isPaused()
	{
		return this.offset >= 0;
	}

	public void copy(PausedMorph<T> pause)
	{
		this.offset = pause.offset;
		this.previous = pause.previous;
		this.clazz = pause.clazz;
	}

	public void set(AbstractMorph previous, Class<T> clazz, int offset)
	{
		this.offset = offset;

		if (previous != null && previous.getClass() == clazz)
		{
			this.previous = clazz.cast(previous);
			this.clazz = clazz;
		}
		else
		{
			this.previous = null;
			this.clazz = null;
		}
	}

	public void reset()
	{
		this.offset = -1;
		this.previous = null;
		this.clazz = null;
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

	public void fromNBT(NBTTagCompound tag, Class<T> clazz)
	{
		this.reset();

		if (tag.hasKey("Pause"))
		{
			this.offset = tag.getInteger("Pause");

			if (tag.hasKey("PausePrevious"))
			{
				AbstractMorph morph = MorphManager.INSTANCE.morphFromNBT(tag.getCompoundTag("PausePrevious"));

				if (morph != null && morph.getClass() == clazz)
				{
					this.previous = clazz.cast(morph);
				}
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

			if (this.previous instanceof CustomMorph)
			{
				animation.last = ((CustomMorph) this.previous).getCurrentPose();
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