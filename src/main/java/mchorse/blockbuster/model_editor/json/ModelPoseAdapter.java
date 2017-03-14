package mchorse.blockbuster.model_editor.json;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import mchorse.metamorph.api.models.Model;
import mchorse.metamorph.api.models.Model.Pose;

/**
 * Model limb adapter
 */
public class ModelPoseAdapter implements JsonSerializer<Model.Pose>
{
    @Override
    public JsonElement serialize(Pose src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonElement serial = ModelAdapter.plainGSON.toJsonTree(src, typeOfSrc);
        JsonObject map = serial.getAsJsonObject();
        JsonObject limbs = new JsonObject();

        map.remove("limbs");

        for (Map.Entry<String, Model.Transform> limb : src.limbs.entrySet())
        {
            Model.Transform trans = limb.getValue();
            JsonObject transform = new JsonObject();
            boolean empty = true;

            if (!this.isDefault(trans.translate, 0))
            {
                this.addFloatArray(transform, "translate", trans.translate);
                empty = false;
            }

            if (!this.isDefault(trans.rotate, 0))
            {
                this.addFloatArray(transform, "rotate", trans.rotate);
                empty = false;
            }

            if (!this.isDefault(trans.scale, 1))
            {
                this.addFloatArray(transform, "scale", trans.scale);
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

    private boolean isDefault(float[] array, float defaultValue)
    {
        return array[0] == defaultValue && array[1] == defaultValue && array[2] == defaultValue;
    }

    private void addFloatArray(JsonObject map, String name, float[] array)
    {
        JsonArray jsonArray = new JsonArray();

        for (float num : array)
        {
            jsonArray.add(new JsonPrimitive(num));
        }

        map.add(name, jsonArray);
    }
}