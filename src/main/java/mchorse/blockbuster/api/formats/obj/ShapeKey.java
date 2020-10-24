package mchorse.blockbuster.api.formats.obj;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;

public class ShapeKey
{
	public String name;
	public float value;
	public boolean relative;

	public ShapeKey()
	{}

	public ShapeKey(String name, float value)
	{
		this.name = name;
		this.value = value;
	}

	public ShapeKey(String name, float value, boolean relative)
	{
		this(name, value);
		this.relative = relative;
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

			return this.value == shape.value && Objects.equals(this.name, shape.name) && this.relative == shape.relative;
		}

		return super.equals(obj);
	}

	public ShapeKey copy()
	{
		ShapeKey shapeKey = new ShapeKey(this.name, this.value);

		shapeKey.relative = this.relative;

		return shapeKey;
	}

	public NBTBase toNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();

		tag.setString("Name", this.name);
		tag.setFloat("Value", this.value);
		tag.setBoolean("Relative", this.relative);

		return tag;
	}

	public void fromNBT(NBTTagCompound key)
	{
		this.name = key.getString("Name");
		this.value = key.getFloat("Value");
		this.relative = key.getBoolean("Relative");
	}
}