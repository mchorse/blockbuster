package mchorse.blockbuster.client.particles.components.appearance;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BedrockComponentAppearanceTinting extends BedrockComponentBase
{
	public Color color;

	public static int parseColor(JsonElement element)
	{
		if (element.isJsonPrimitive())
		{
			String hex = element.getAsString();

			if (hex.startsWith("#") && (hex.length() == 7 || hex.length() == 9))
			{

			}
		}
		else if (element.isJsonArray())
		{
			JsonArray array = element.getAsJsonArray();

			if (array.size() == 3 || array.size() == 4)
			{
				float r = array.get(0).getAsFloat();
				float g = array.get(1).getAsFloat();
				float b = array.get(2).getAsFloat();
				float a = array.size() == 4 ? array.get(3).getAsFloat() : 1F;

				return (int) (a * 255) << 24 + (int) (r * 255) << 16 + (int) (g * 255) << 8 + (int) (b * 255);
			}
		}

		return 0xff000000;
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
				this.color = new Color.Static(parseColor(color));
			}
			else if (color.isJsonObject())
			{
				JsonObject colorObject = color.getAsJsonObject();
				JsonElement gradient = colorObject.get("gradient");

				MolangExpression expression = parser.parseJson(colorObject.get("interpolant"));
				List<Color.Gradient.ColorStop> colorStops = new ArrayList<Color.Gradient.ColorStop>();

				if (gradient.isJsonObject())
				{
					for (Map.Entry<String, JsonElement> entry : gradient.getAsJsonObject().entrySet())
					{
						colorStops.add(new Color.Gradient.ColorStop(Float.parseFloat(entry.getKey()), parseColor(entry.getValue())));
					}

					Collections.sort(colorStops, (a, b) -> a.stop > b.stop ? 1 : -1);
				}
				else if (gradient.isJsonArray())
				{
					JsonArray colors = gradient.getAsJsonArray();

					int i = 0;

					for (JsonElement stop : colors)
					{
						colorStops.add(new Color.Gradient.ColorStop(i / (float) (colors.size() - 1), parseColor(stop)));

						i ++;
					}
				}

				this.color = new Color.Gradient(colorStops, expression);
			}
		}

		return super.fromJson(element, parser);
	}
}