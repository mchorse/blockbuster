package mchorse.blockbuster.model_editor.json;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import mchorse.metamorph.api.models.Model;

/**
 * JSON model adapter
 */
public class ModelAdapter implements JsonSerializer<Model>
{
    public static Gson plainGSON = new GsonBuilder().create();

    @Override
    public JsonElement serialize(Model src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonElement serial = plainGSON.toJsonTree(src, typeOfSrc);
        JsonObject map = serial.getAsJsonObject();

        map.remove("model");
        map.remove("defaultTexture");
        map.remove("scale");
        map.remove("limbs");
        map.remove("poses");

        if (!src.model.isEmpty())
        {
            map.addProperty("model", src.model);
        }

        if (src.defaultTexture != null)
        {
            map.addProperty("default", src.defaultTexture.toString());
        }

        if (src.scale[0] != 1 || src.scale[1] != 1 || src.scale[2] != 1)
        {
            JsonArray array = new JsonArray();

            array.add(new JsonPrimitive(src.scale[0]));
            array.add(new JsonPrimitive(src.scale[1]));
            array.add(new JsonPrimitive(src.scale[2]));

            map.add("scale", array);
        }

        map.add("limbs", context.serialize(src.limbs));
        map.add("poses", context.serialize(src.poses));

        return map;
    }
}