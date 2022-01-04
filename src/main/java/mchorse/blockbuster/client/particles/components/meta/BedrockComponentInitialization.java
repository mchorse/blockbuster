package mchorse.blockbuster.client.particles.components.meta;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentEmitterInitialize;
import mchorse.blockbuster.client.particles.components.IComponentEmitterUpdate;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.molang.MolangException;
import mchorse.mclib.math.molang.MolangParser;
import mchorse.mclib.math.molang.expressions.MolangAssignment;
import mchorse.mclib.math.molang.expressions.MolangExpression;
import mchorse.mclib.math.molang.expressions.MolangMultiStatement;

import java.util.Map;

public class BedrockComponentInitialization extends BedrockComponentBase implements IComponentEmitterInitialize, IComponentEmitterUpdate
{
    /* Standard BedrockEdition variables - global inside an emitter */
    public MolangExpression creation = MolangParser.ZERO;
    public MolangExpression update = MolangParser.ZERO;

    /* Blockbuster specific expression - local inside a particle (added by Chryfi) */
    public MolangExpression particleUpdate = MolangParser.ZERO;

    public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
    {
        if (!elem.isJsonObject()) return super.fromJson(elem, parser);

        JsonObject element = elem.getAsJsonObject();

        if (element.has("creation_expression")) this.creation = parser.parseGlobalJson(element.get("creation_expression"));
        if (element.has("per_update_expression")) this.update = parser.parseGlobalJson(element.get("per_update_expression"));
        if (element.has("particle_update_expression")) this.particleUpdate = parser.parseGlobalJson(element.get("particle_update_expression"));

        return super.fromJson(element, parser);
    }

    @Override
    public JsonElement toJson()
    {
        JsonObject object = new JsonObject();

        if (!MolangExpression.isZero(this.creation)) object.add("creation_expression", this.creation.toJson());
        if (!MolangExpression.isZero(this.update)) object.add("per_update_expression", this.update.toJson());
        if (!MolangExpression.isZero(this.particleUpdate)) object.add("particle_update_expression", this.particleUpdate.toJson());

        return object;
    }

    @Override
    public void apply(BedrockEmitter emitter)
    {
        emitter.initialValues.clear();

        this.creation.get();
        this.cacheInitialValues(this.creation, emitter);

        if (emitter.variables != null)
        {
            for (Map.Entry<String, IValue> entry : emitter.variables.entrySet())
            {
                emitter.initialValues.put(entry.getKey(), entry.getValue().get().doubleValue());
            }
        }
    }

    @Override
    public void update(BedrockEmitter emitter)
    {
        this.update.get();
        this.cacheInitialValues(this.update, emitter);

        emitter.replaceVariables();
    }

    private void cacheInitialValues(MolangExpression e, BedrockEmitter emitter)
    {
        if (e instanceof MolangMultiStatement)
        {
            MolangMultiStatement statement = (MolangMultiStatement) e;

            for (MolangExpression expression : statement.expressions)
            {
                if (expression instanceof MolangAssignment)
                {
                    this.cacheInitialValue((MolangAssignment) expression, emitter);
                }
            }
        }
        else if (e instanceof MolangAssignment)
        {
            this.cacheInitialValue((MolangAssignment) e, emitter);
        }
    }

    private void cacheInitialValue(MolangAssignment assignment, BedrockEmitter emitter)
    {
        emitter.initialValues.put(assignment.variable.getName(), assignment.variable.get().doubleValue());
    }
}