package mchorse.blockbuster.client.particles.components;

import com.google.gson.JsonObject;

public abstract class BedrockComponentBase
{
	public BedrockComponentBase fromJson(JsonObject element)
	{
		return this;
	}
}