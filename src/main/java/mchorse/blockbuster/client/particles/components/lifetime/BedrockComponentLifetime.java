package mchorse.blockbuster.client.particles.components.lifetime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentEmitterUpdate;
import mchorse.mclib.math.Constant;
import mchorse.mclib.math.molang.MolangException;
import mchorse.mclib.math.molang.MolangParser;
import mchorse.mclib.math.molang.expressions.MolangExpression;
import mchorse.mclib.math.molang.expressions.MolangValue;

public abstract class BedrockComponentLifetime extends BedrockComponentBase implements IComponentEmitterUpdate
{
    public static final MolangExpression DEFAULT_ACTIVE = new MolangValue(null, new Constant(10));

    public MolangExpression activeTime = DEFAULT_ACTIVE;

    public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
    {
        if (!elem.isJsonObject())
        {
            return super.fromJson(elem, parser);
        }

        JsonObject element = elem.getAsJsonObject();

        if (element.has(this.getPropertyName()))
        {
            this.activeTime = parser.parseJson(element.get(this.getPropertyName()));
        }

        return super.fromJson(element, parser);
    }

    @Override
    public JsonElement toJson()
    {
        JsonObject object = new JsonObject();

        if (!MolangExpression.isConstant(this.activeTime, 10))
        {
            object.add(this.getPropertyName(), this.activeTime.toJson());
        }

        return object;
    }

    protected String getPropertyName()
    {
        return "active_time";
    }

    @Override
    public int getSortingIndex()
    {
        return -10;
    }
}