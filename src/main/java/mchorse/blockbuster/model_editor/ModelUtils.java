package mchorse.blockbuster.model_editor;

import java.io.StringWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import mchorse.metamorph.api.json.ModelAdapter;
import mchorse.metamorph.api.json.ModelLimbAdapter;
import mchorse.metamorph.api.json.ModelPoseAdapter;
import mchorse.metamorph.api.models.Model;

/**
 * Model utilities
 *
 * This code might be transferred to Metamorph, since this code is actually
 * supposed to be in {@link Model} class.
 */
public class ModelUtils
{
    /**
     * Save model to JSON
     *
     * This method is responsible for making the JSON output pretty printed and
     * 4 spaces indented (fuck 2 space indentation).
     */
    public static String toJson(Model model)
    {
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();

        builder.registerTypeAdapter(Model.class, new ModelAdapter());
        builder.registerTypeAdapter(Model.Limb.class, new ModelLimbAdapter());
        builder.registerTypeAdapter(Model.Pose.class, new ModelPoseAdapter());

        Gson gson = builder.create();
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        jsonWriter.setIndent("    ");
        gson.toJson(model, Model.class, jsonWriter);

        String output = writer.toString();

        /* Prettify arrays */
        output = output.replaceAll("\\n\\s+(?=-?\\d|\\])", " ");

        return output;
    }

    /**
     * Copy model from model
     */
    public static void copy(Model from, Model to)
    {
        to.defaultTexture = from.defaultTexture;
        to.texture = from.texture;
        to.scheme = from.scheme;
        to.scale = from.scale;
        to.model = from.model;
        to.name = from.name;
        to.providesObj = from.providesObj;

        to.poses = from.poses;
        to.limbs = from.limbs;
    }
}