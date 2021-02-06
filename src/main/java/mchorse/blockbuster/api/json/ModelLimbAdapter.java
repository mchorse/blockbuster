package mchorse.blockbuster.api.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelLimb.ArmorSlot;
import mchorse.blockbuster.api.ModelLimb.Holding;

/**
 * Model limb adapter
 */
public class ModelLimbAdapter implements JsonSerializer<ModelLimb>, JsonDeserializer<ModelLimb>
{
    @Override
    public JsonElement serialize(ModelLimb src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonElement serial = ModelAdapter.plainGSON.toJsonTree(src, typeOfSrc);
        JsonObject map = serial.getAsJsonObject();

        map.remove("sizeOffset");
        map.remove("itemScale");
        map.remove("holding");
        map.remove("slot");
        map.remove("parent");
        map.remove("name");
        map.remove("opacity");
        map.remove("color");

        if (src.sizeOffset != 0)
        {
            map.addProperty("sizeOffset", src.sizeOffset);
        }

        if (src.itemScale != 1F)
        {
            map.addProperty("itemScale", src.itemScale);
        }

        if (src.holding != Holding.NONE)
        {
            map.addProperty("holding", src.holding == Holding.RIGHT ? "right" : "left");
        }

        if (src.slot != null && src.slot != ArmorSlot.NONE)
        {
            map.addProperty("slot", src.slot.name);
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

        this.addBoolean(map, "lighting", src.lighting, true);
        this.addBoolean(map, "shading", src.shading, true);
        this.addBoolean(map, "smooth", src.smooth, false);
        this.addBoolean(map, "is3D", src.is3D, false);

        this.addBoolean(map, "hold", src.hold, true);
        this.addBoolean(map, "mirror", src.mirror, false);
        this.addBoolean(map, "lookX", src.lookX, false);
        this.addBoolean(map, "lookY", src.lookY, false);
        this.addBoolean(map, "idle", src.idle, false);
        this.addBoolean(map, "swinging", src.swinging, false);
        this.addBoolean(map, "swiping", src.swiping, false);
        this.addBoolean(map, "invert", src.invert, false);
        this.addBoolean(map, "wheel", src.wheel, false);
        this.addBoolean(map, "wing", src.wing, false);
        this.addBoolean(map, "roll", src.roll, false);
        this.addBoolean(map, "cape", src.cape, false);

        if (!ModelPoseAdapter.isDefault(src.origin, 0F))
        {
            ModelPoseAdapter.addFloatArray(map, "origin", src.origin);
        }

        return map;
    }

    @Override
    public ModelLimb deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        ModelLimb limb = ModelAdapter.plainGSON.fromJson(json, ModelLimb.class);
        JsonObject object = json.getAsJsonObject();

        if (object.has("looking") && object.get("looking").isJsonPrimitive())
        {
            boolean looking = object.get("looking").getAsBoolean();

            limb.lookX = limb.lookY = looking;
        }

        if (object.has("holding") && object.get("holding").isJsonPrimitive())
        {
            String holding = object.get("holding").getAsString();

            if (holding.equals("right"))
            {
                limb.holding = Holding.RIGHT;
            }
            else if (holding.equals("left"))
            {
                limb.holding = Holding.LEFT;
            }
        }

        if (limb.holding == null)
        {
            limb.holding = Holding.NONE;
        }

        if (object.has("slot"))
        {
            try
            {
                limb.slot = ArmorSlot.fromName(object.get("slot").getAsString());
            }
            catch (Exception e)
            {}
        }
        else
        {
            limb.slot = ArmorSlot.NONE;
        }

        return limb;
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