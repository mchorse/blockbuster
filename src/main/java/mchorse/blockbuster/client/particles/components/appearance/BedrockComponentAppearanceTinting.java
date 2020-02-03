package mchorse.blockbuster.client.particles.components.appearance;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentParticleRender;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;
import mchorse.blockbuster.client.particles.molang.expressions.MolangValue;
import mchorse.mclib.math.Constant;
import net.minecraft.client.renderer.VertexBuffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BedrockComponentAppearanceTinting extends BedrockComponentBase implements IComponentParticleRender
{
	public Color color;

	@Override
	public int getSortingIndex()
	{
		return -10;
	}

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
				this.color = this.parseColor(color, parser);
			}
			else if (color.isJsonObject())
			{
				this.color = this.parseGradient(color.getAsJsonObject(), parser);
			}
		}

		return super.fromJson(element, parser);
	}

	/**
	 * Parse a single color either in hex string format or JSON array
	 * (this should parse both RGB and RGBA expressions)
	 */
	private Color.Solid parseColor(JsonElement element, MolangParser parser) throws MolangException
	{
		MolangExpression r = MolangParser.ONE;
		MolangExpression g = MolangParser.ONE;
		MolangExpression b = MolangParser.ONE;
		MolangExpression a = MolangParser.ONE;

		if (element.isJsonPrimitive())
		{
			String hex = element.getAsString();

			if (hex.startsWith("#") && (hex.length() == 7 || hex.length() == 9))
			{
				try
				{
					int color = Integer.parseInt(hex.substring(1), 16);
					float hr = (color >> 16 & 0xff) / 255F;
					float hg = (color >> 8 & 0xff) / 255F;
					float hb = (color & 0xff) / 255F;
					float ha = hex.length() == 9 ? (color >> 24 & 0xff) : 1;

					r = new MolangValue(parser, new Constant(hr));
					g = new MolangValue(parser, new Constant(hg));
					b = new MolangValue(parser, new Constant(hb));
					a = new MolangValue(parser, new Constant(ha));
				}
				catch (Exception e) {}
			}
		}
		else if (element.isJsonArray())
		{
			JsonArray array = element.getAsJsonArray();

			if (array.size() == 3 || array.size() == 4)
			{
				r = parser.parseJson(array.get(0));
				g = parser.parseJson(array.get(1));
				b = parser.parseJson(array.get(2));

				if (array.size() == 4)
				{
					a = parser.parseJson(array.get(3));
				}
			}
		}

		return new Color.Solid(r, g, b, a);
	}

	/**
	 * Parse a gradient
	 */
	private Color parseGradient(JsonObject color, MolangParser parser) throws MolangException
	{
		JsonElement gradient = color.get("gradient");

		MolangExpression expression = parser.parseJson(color.get("interpolant"));
		List<Color.Gradient.ColorStop> colorStops = new ArrayList<Color.Gradient.ColorStop>();

		if (gradient.isJsonObject())
		{
			for (Map.Entry<String, JsonElement> entry : gradient.getAsJsonObject().entrySet())
			{
				colorStops.add(new Color.Gradient.ColorStop(Float.parseFloat(entry.getKey()), parseColor(entry.getValue(), parser)));
			}

			Collections.sort(colorStops, (a, b) -> a.stop > b.stop ? 1 : -1);
		}
		else if (gradient.isJsonArray())
		{
			JsonArray colors = gradient.getAsJsonArray();

			int i = 0;

			for (JsonElement stop : colors)
			{
				colorStops.add(new Color.Gradient.ColorStop(i / (float) (colors.size() - 1), parseColor(stop, parser)));

				i ++;
			}
		}

		return new Color.Gradient(colorStops, expression);
	}

	@Override
	public void preRender(BedrockEmitter emitter, float partialTicks)
	{}

	@Override
	public void render(BedrockEmitter emitter, BedrockParticle particle, VertexBuffer builder, float partialTicks)
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
}