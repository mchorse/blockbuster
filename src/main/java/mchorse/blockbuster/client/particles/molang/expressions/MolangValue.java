package mchorse.blockbuster.client.particles.molang.expressions;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.mclib.math.Constant;
import mchorse.mclib.math.IValue;

public class MolangValue extends MolangExpression
{
	public IValue value;
	public boolean addReturn;

	public MolangValue(MolangParser context, IValue value)
	{
		super(context);

		this.value = value;
	}

	public MolangExpression addReturn()
	{
		this.addReturn = true;

		return this;
	}

	@Override
	public double get()
	{
		return this.value.get();
	}

	@Override
	public String toString()
	{
		return (this.addReturn ? MolangParser.RETURN : "") + this.value.toString();
	}

	@Override
	public JsonElement toJson()
	{
		if (this.value instanceof Constant)
		{
			return new JsonPrimitive(this.value.get());
		}

		return super.toJson();
	}
}