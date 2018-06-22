package mchorse.blockbuster.api.json;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.ModelTransform;

/**
 * Model limb adapter
 */
public class ModelPoseAdapter implements JsonSerializer<ModelPose>
{
    @Override
    public JsonElement serialize(ModelPose src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonElement serial = ModelAdapter.plainGSON.toJsonTree(src, typeOfSrc);
        JsonObject map = serial.getAsJsonObject();
        JsonObject limbs = new JsonObject();

        map.remove("limbs");

        for (Map.Entry<String, ModelTransform> limb : src.limbs.entrySet())
        {
            ModelTransform trans = limb.getValue();
            JsonObject transform = new JsonObject();
            boolean empty = true;

            if (!isDefault(trans.translate, 0))
            {
                addFloatArray(transform, "translate", trans.translate);
                empty = false;
            }

            if (!isDefault(trans.rotate, 0))
            {
                addFloatArray(transform, "rotate", trans.rotate);
                empty = false;
            }

            if (!isDefault(trans.scale, 1))
            {
                addFloatArray(transform, "scale", trans.scale);
                empty = false;
            }

            if (!empty)
            {
                limbs.add(limb.getKey(), transform);
            }
        }

        map.add("limbs", limbs);

        return map;
    }

    public static boolean isDefault(float[] array, float defaultValue)
    {
        return array[0] == defaultValue && array[1] == defaultValue && array[2] == defaultValue;
    }

    public static void addFloatArray(JsonObject map, String name, float[] array)
    {
        JsonArray jsonArray = new JsonArray();

        for (float num : array)
        {
            jsonArray.add(new JsonPrimitive(num));
        }

        map.add(name, jsonArray);
    }
}