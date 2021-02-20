package mchorse.blockbuster.client.particles.components.meta;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentEmitterInitialize;
import mchorse.blockbuster.client.particles.components.IComponentEmitterUpdate;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangAssignment;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;
import mchorse.blockbuster.client.particles.molang.expressions.MolangMultiStatement;
import mchorse.mclib.math.IValue;

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

        if (this.creation instanceof MolangMultiStatement)
        {
            MolangMultiStatement statement = (MolangMultiStatement) this.creation;

            for (MolangExpression expression : statement.expressions)
            {
                if (expression instanceof MolangAssignment)
                {
                    MolangAssignment assignment = (MolangAssignment) expression;

                    emitter.initialValues.put(assignment.variable.getName(), assignment.variable.get());
                }
            }
        }

        if (emitter.variables != null)
        {
            for (Map.Entry<String, IValue> entry : emitter.variables.entrySet())
            {
                emitter.initialValues.put(entry.getKey(), entry.getValue().get());
            }
        }
    }

    @Override
    public void update(BedrockEmitter emitter)
    {
        this.update.get();
        emitter.replaceVariables();
    }
}