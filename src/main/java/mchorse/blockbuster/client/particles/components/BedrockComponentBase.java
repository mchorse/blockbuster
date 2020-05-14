package mchorse.blockbuster.client.particles.components;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;

public abstract class BedrockComponentBase
{
	public BedrockComponentBase fromJson(JsonElement element, MolangParser parser) throws MolangException
	{
		return this;
	}

	public JsonElement toJson()
	{
		return new JsonObject();
	}

	public boolean canBeEmpty()
	{
		return false;
	}
}