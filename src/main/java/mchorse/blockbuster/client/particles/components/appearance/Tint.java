package mchorse.blockbuster.client.particles.components.appearance;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mchorse.blockbuster.client.particles.BedrockSchemeJsonAdapter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.mclib.math.Constant;
import mchorse.mclib.math.functions.rounding.Round;
import mchorse.mclib.math.molang.MolangException;
import mchorse.mclib.math.molang.MolangParser;
import mchorse.mclib.math.molang.expressions.MolangExpression;
import mchorse.mclib.math.molang.expressions.MolangValue;
import mchorse.mclib.utils.Interpolations;
import mchorse.mclib.utils.MathUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class Tint
{
    /**
     * Parse a single color either in hex string format or JSON array
     * (this should parse both RGB and RGBA expressions)
     */
    public static Tint.Solid parseColor(JsonElement element, MolangParser parser) throws MolangException
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
                boolean hasAlpha = hex.length() == 9;

                try
                {
                    /* Apparently, Integer.parseInt() can't parse hex
                     * numbers that are longer than 6 hexadecimals... */
                    int color = Integer.parseInt(hex.substring(hasAlpha ? 3 : 1), 16);
                    float hr = (color >> 16 & 0xff) / 255F;
                    float hg = (color >> 8 & 0xff) / 255F;
                    float hb = (color & 0xff) / 255F;
                    float ha = hasAlpha ? Integer.parseInt(hex.substring(1, 3), 16) / 255F : 1;

                    r = new MolangValue(parser, new Constant(hr));
                    g = new MolangValue(parser, new Constant(hg));
                    b = new MolangValue(parser, new Constant(hb));
                    a = new MolangValue(parser, new Constant(ha));
                }
                catch (Exception e)
                {}
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

        return new Tint.Solid(r, g, b, a);
    }

    /**
     * Parse a gradient
     */
    public static Tint parseGradient(JsonObject color, MolangParser parser) throws MolangException
    {
        JsonElement gradient = color.get("gradient");

        MolangExpression expression = MolangParser.ZERO;
        List<Tint.Gradient.ColorStop> colorStops = new ArrayList<Gradient.ColorStop>();
        boolean equal = true;

        if (gradient.isJsonObject())
        {
            for (Map.Entry<String, JsonElement> entry : gradient.getAsJsonObject().entrySet())
            {
                Tint.Solid stopColor = parseColor(entry.getValue(), parser);

                colorStops.add(new Tint.Gradient.ColorStop(Float.parseFloat(entry.getKey()), stopColor));
            }

            colorStops.sort((a, b) -> Float.compare(a.stop, b.stop));
            equal = false;
        }
        else if (gradient.isJsonArray())
        {
            JsonArray colors = gradient.getAsJsonArray();

            int i = 0;

            for (JsonElement stop : colors)
            {
                colorStops.add(new Tint.Gradient.ColorStop(i / (float) (colors.size() - 1), parseColor(stop, parser)));

                i ++;
            }
        }

        float range = colorStops.get(colorStops.size() - 1).stop;

        for (Gradient.ColorStop stop : colorStops)
        {
            stop.stop /= range;
        }

        if (color.has("interpolant"))
        {
            expression = parser.parseJson(color.get("interpolant"));
        }

        return new Tint.Gradient(colorStops, range, expression, equal);
    }

    public abstract void compute(BedrockParticle particle);

    public abstract JsonElement toJson();

    /**
     * Solid color (not necessarily static)
     */
    public static class Solid extends Tint
    {
        public MolangExpression r;
        public MolangExpression g;
        public MolangExpression b;
        public MolangExpression a;

        public Solid(MolangExpression r, MolangExpression g, MolangExpression b, MolangExpression a)
        {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }

        public Solid()
        {
            this.r = MolangParser.ONE;
            this.g = MolangParser.ONE;
            this.b = MolangParser.ONE;
            this.a = MolangParser.ONE;
        }

        public boolean isConstant()
        {
            return MolangExpression.isExpressionConstant(this.r) && MolangExpression.isExpressionConstant(this.g)
                && MolangExpression.isExpressionConstant(this.b) && MolangExpression.isExpressionConstant(this.a);
        }

        @Override
        public void compute(BedrockParticle particle)
        {
            particle.r = (float) this.r.get();
            particle.g = (float) this.g.get();
            particle.b = (float) this.b.get();
            particle.a = (float) this.a.get();
        }

        @Override
        public JsonElement toJson()
        {
            JsonArray array = new JsonArray();

            if (MolangExpression.isOne(this.r) && MolangExpression.isOne(this.g) && MolangExpression.isOne(this.b) && MolangExpression.isOne(this.a))
            {
                return array;
            }

            array.add(this.r.toJson());
            array.add(this.g.toJson());
            array.add(this.b.toJson());
            array.add(this.a.toJson());

            return array;
        }

        public JsonElement toHexJson()
        {
            int r = (int) (this.r.get() * 255) & 0xff;
            int g = (int) (this.g.get() * 255) & 0xff;
            int b = (int) (this.b.get() * 255) & 0xff;
            int a = (int) (this.a.get() * 255) & 0xff;

            String hex = "#";

            if (a < 255)
            {
                hex += StringUtils.leftPad(Integer.toHexString(a), 2, "0").toUpperCase();
            }

            hex += StringUtils.leftPad(Integer.toHexString(r), 2, "0").toUpperCase();
            hex += StringUtils.leftPad(Integer.toHexString(g), 2, "0").toUpperCase();
            hex += StringUtils.leftPad(Integer.toHexString(b), 2, "0").toUpperCase();

            return new JsonPrimitive(hex);
        }

        public void lerp(BedrockParticle particle, float factor)
        {
            particle.r = Interpolations.lerp(particle.r, (float) this.r.get(), factor);
            particle.g = Interpolations.lerp(particle.g, (float) this.g.get(), factor);
            particle.b = Interpolations.lerp(particle.b, (float) this.b.get(), factor);
            particle.a = Interpolations.lerp(particle.a, (float) this.a.get(), factor);
        }
    }

    /**
     * Gradient color, instead of using formulas, you can just specify a couple of colors
     * and an expression at which color it would stop
     */
    public static class Gradient extends Tint
    {
        public List<ColorStop> stops;
        public MolangExpression interpolant;
        public float range = 1;
        public boolean equal;

        public Gradient(List<ColorStop> stops, float range, MolangExpression interpolant, boolean equal)
        {
            this.stops = stops;
            this.range = range;
            this.interpolant = interpolant;
            this.equal = equal;
        }

        public Gradient()
        {
            this.stops = new ArrayList<>();
            this.stops.add(new ColorStop(0, new Tint.Solid(new MolangValue(null, new Constant(1F)), new MolangValue(null, new Constant(1F)), new MolangValue(null, new Constant(1F)), new MolangValue(null, new Constant(1F)))));
            this.stops.add(new ColorStop(1, new Tint.Solid(new MolangValue(null, new Constant(0F)), new MolangValue(null, new Constant(0F)), new MolangValue(null, new Constant(0F)), new MolangValue(null, new Constant(1F)))));
            this.interpolant = MolangParser.ZERO;
            this.equal = false;
        }

        public void sort()
        {
            this.stops.sort((a, b) -> Float.compare(a.stop, b.stop));
        }

        @Override
        public void compute(BedrockParticle particle)
        {
            int length = this.stops.size();

            if (length == 0)
            {
                particle.r = particle.g = particle.b = particle.a = 1;

                return;
            }
            else if (length == 1)
            {
                this.stops.get(0).color.compute(particle);

                return;
            }

            double factor = this.interpolant.get();

            factor = MathUtils.clamp(factor, 0, 1);

            ColorStop prev = this.stops.get(0);

            if (factor < prev.getStop(this.range))
            {
                prev.color.compute(particle);

                return;
            }

            for (int i = 1; i < length; i ++)
            {
                ColorStop stop = this.stops.get(i);

                if (stop.getStop(this.range) > factor)
                {
                    prev.color.compute(particle);
                    stop.color.lerp(particle, (float) (factor - prev.getStop(this.range)) / (stop.getStop(this.range) - prev.getStop(this.range)));

                    return;
                }

                prev = stop;
            }

            prev.color.compute(particle);
        }

        @Override
        public JsonElement toJson()
        {
            JsonObject object = new JsonObject();
            JsonElement color;

            if (this.equal)
            {
                JsonArray gradient = new JsonArray();

                for (ColorStop stop : this.stops)
                {
                    gradient.add(stop.color.toHexJson());
                }

                color = gradient;
            }
            else
            {
                JsonObject gradient = new JsonObject();

                for (ColorStop stop : this.stops)
                {
                    gradient.add(String.valueOf(stop.getStop(this.range)), stop.color.toHexJson());
                }

                color = gradient;
            }

            if (!BedrockSchemeJsonAdapter.isEmpty(color))
            {
                object.add("gradient", color);
            }

            if (!MolangExpression.isZero(this.interpolant))
            {
                object.add("interpolant", this.interpolant.toJson());
            }

            return object;
        }

        public static class ColorStop
        {
            public float stop;
            public Tint.Solid color;

            public ColorStop(float stop, Tint.Solid color)
            {
                this.stop = stop;
                this.color = color;
            }

            public float getStop(float range)
            {
                return this.stop * range;
            }
        }
    }
}