package mchorse.blockbuster.api.formats.obj;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;

public class ShapeKey
{
	public String name;
	public float value;

	public ShapeKey(String name, float value)
	{
		this.name = name;
		this.value = value;
	}

	public ShapeKey setValue(float value)
	{
		this.value = value;

		return this;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ShapeKey)
		{
			ShapeKey shape = (ShapeKey) obj;

			return this.value == shape.value && Objects.equals(this.name, shape.name);
		}

		return super.equals(obj);
	}

	public ShapeKey copy()
	{
		return new ShapeKey(this.name, this.value);
	}

	public NBTBase toNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();

		tag.setString("Name", this.name);
		tag.setFloat("Value", this.value);

		return tag;
	}
}