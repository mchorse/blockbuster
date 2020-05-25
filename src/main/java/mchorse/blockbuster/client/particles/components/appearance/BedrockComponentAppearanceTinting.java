package mchorse.blockbuster.client.particles.components.appearance;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.BedrockSchemeJsonAdapter;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentParticleRender;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import net.minecraft.client.renderer.BufferBuilder;

public class BedrockComponentAppearanceTinting extends BedrockComponentBase implements IComponentParticleRender
{
	public Tint color = new Tint.Solid(MolangParser.ONE, MolangParser.ONE, MolangParser.ONE, MolangParser.ONE);

	@Override
	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject()) return super.fromJson(elem, parser);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("color"))
		{
			JsonElement color = element.get("color");

			if (color.isJsonArray() || color.isJsonPrimitive())
			{
				this.color = Tint.parseColor(color, parser);
			}
			else if (color.isJsonObject())
			{
				this.color = Tint.parseGradient(color.getAsJsonObject(), parser);
			}
		}

		return super.fromJson(element, parser);
	}

	@Override
	public JsonElement toJson()
	{
		JsonObject object = new JsonObject();
		JsonElement element = this.color.toJson();

		if (!BedrockSchemeJsonAdapter.isEmpty(element))
		{
			object.add("color", element);
		}

		return object;
	}

	/* Interface implementations */

	@Override
	public void preRender(BedrockEmitter emitter, float partialTicks)
	{}

	@Override
	public void render(BedrockEmitter emitter, BedrockParticle particle, BufferBuilder builder, float partialTicks)
	{
		this.renderOnScreen(particle, 0, 0, 0, 0);
	}

	@Override
	public void renderOnScreen(BedrockParticle particle, int x, int y, float scale, float partialTicks)
	{
		if (this.color != null)
		{
			this.color.compute(particle);
		}
		else
		{
			particle.r = particle.g = particle.b = particle.a = 1;
		}
	}

	@Override
	public void postRender(BedrockEmitter emitter, float partialTicks)
	{}

	@Override
	public int getSortingIndex()
	{
		return -10;
	}
}