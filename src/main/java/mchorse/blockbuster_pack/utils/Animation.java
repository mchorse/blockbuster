package mchorse.blockbuster_pack.utils;

import mchorse.mclib.utils.Interpolation;
import net.minecraft.nbt.NBTTagCompound;

public class Animation
{
	public boolean animates;
	public boolean ignored;
	public int duration = 10;
	public Interpolation interp = Interpolation.LINEAR;

	public int progress;
	public boolean paused;

	public void pause()
	{
		this.paused = true;
	}

	public float getFactor(float partialTicks)
	{
		return (this.progress + (this.paused ? 0 : partialTicks)) / (float) this.duration;
	}

	public void reset()
	{
		this.progress = this.duration;
	}

	public void merge(Animation animation)
	{
		this.copy(animation);
		this.progress = 0;
	}

	public void copy(Animation animation)
	{
		this.animates = animation.animates;
		this.duration = animation.duration;
		this.interp = animation.interp;
		this.ignored = animation.ignored;
		this.paused = animation.paused;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Animation)
		{
			Animation animation = (Animation) obj;

			return this.animates == animation.animates &&
					this.duration == animation.duration &&
					this.ignored == animation.ignored &&
					this.interp == animation.interp;
		}

		return super.equals(obj);
	}

	public void update()
	{
		if (this.animates && !this.paused)
		{
			this.progress++;
		}
	}

	public boolean isInProgress()
	{
		return this.paused || (this.animates && this.progress < this.duration);
	}

	public NBTTagCompound toNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();

		if (this.animates) tag.setBoolean("Animates", this.animates);
		if (this.ignored) tag.setBoolean("Ignored", this.ignored);
		if (this.duration != 10) tag.setInteger("Duration", this.duration);
		if (this.interp != Interpolation.LINEAR) tag.setInteger("Interp", this.interp.ordinal());

		return tag;
	}

	public void fromNBT(NBTTagCompound tag)
	{
		if (tag.hasKey("Animates")) this.animates = tag.getBoolean("Animates");
		if (tag.hasKey("Ignored")) this.ignored = tag.getBoolean("Ignored");
		if (tag.hasKey("Duration")) this.duration = tag.getInteger("Duration");
		if (tag.hasKey("Interp")) this.interp = Interpolation.values()[tag.getInteger("Interp")];
	}
}