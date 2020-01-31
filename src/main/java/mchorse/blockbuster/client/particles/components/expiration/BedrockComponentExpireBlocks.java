package mchorse.blockbuster.client.particles.components.expiration;

import com.google.gson.JsonElement;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;

import java.util.ArrayList;
import java.util.List;

public abstract class BedrockComponentExpireBlocks extends BedrockComponentBase
{
	public List<String> blocks = new ArrayList<String>();

	@Override
	public BedrockComponentBase fromJson(JsonElement element)
	{
		if (element.isJsonArray())
		{
			for (JsonElement value : element.getAsJsonArray())
			{
				this.blocks.add(value.getAsString());
			}
		}

		return super.fromJson(element);
	}
}