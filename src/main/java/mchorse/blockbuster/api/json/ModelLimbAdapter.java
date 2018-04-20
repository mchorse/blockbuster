package mchorse.blockbuster.api.json;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.Model.Limb;

/**
 * Model limb adapter
 */
public class ModelLimbAdapter implements JsonSerializer<Model.Limb>
{
    @Override
    public JsonElement serialize(Limb src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonElement serial = ModelAdapter.plainGSON.toJsonTree(src, typeOfSrc);
        JsonObject map = serial.getAsJsonObject();

        map.remove("holding");
        map.remove("parent");
        map.remove("name");
        map.remove("opacity");
        map.remove("color");

        if (!src.holding.isEmpty())
        {
            map.addProperty("holding", src.holding);
        }

        if (!src.parent.isEmpty())
        {
            map.addProperty("parent", src.parent);
        }

        if (!ModelPoseAdapter.isDefault(src.color, 1.0F))
        {
            ModelPoseAdapter.addFloatArray(map, "color", src.color);
        }

        if (src.opacity != 1.0F)
        {
            map.addProperty("opacity", src.opacity);
        }

        this.addBoolean(map, "mirror", src.mirror, false);
        this.addBoolean(map, "looking", src.looking, false);
        this.addBoolean(map, "idle", src.idle, false);
        this.addBoolean(map, "swinging", src.swinging, false);
        this.addBoolean(map, "swiping", src.swiping, false);
        this.addBoolean(map, "invert", src.invert, false);

        if (!ModelPoseAdapter.isDefault(src.origin, 0F))
        {
            ModelPoseAdapter.addFloatArray(map, "origin", src.origin);
        }

        return map;
    }

    /**
     * Add a boolean to the map
     *
     * First remove the property from map, and then, if the given value isn't
     * the default one, add the value
     */
    private void addBoolean(JsonObject map, String name, boolean value, boolean defaultValue)
    {
        map.remove(name);

        if (value != defaultValue)
        {
            map.addProperty(name, value);
        }
    }
}
