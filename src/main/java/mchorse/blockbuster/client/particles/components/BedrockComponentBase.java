package mchorse.blockbuster.client.particles.components;

import com.google.gson.JsonElement;

public abstract class BedrockComponentBase
{
	public BedrockComponentBase fromJson(JsonElement element)
	{
		return this;
	}
}