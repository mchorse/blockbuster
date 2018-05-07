package mchorse.blockbuster.api.json;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import mchorse.blockbuster.api.Model;
import net.minecraft.util.ResourceLocation;

/**
 * Model JSON adapter
 * 
 * This adapter is responsible for only deserializing a {@link Model} instance.
 */
public class ModelAdapter implements JsonDeserializer<Model>, JsonSerializer<Model>
{
    public static Gson plainGSON = new GsonBuilder().create();

    /**
     * Deserializes {@link Model}
     * 
     * This method is responsible mainly from translating "default" field into 
     * {@link ResourceLocation}.
     */
    @Override
    public Model deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        Model model = plainGSON.fromJson(json, Model.class);
        JsonObject object = json.getAsJsonObject();

        if (object.has("default"))
        {
            String type = object.get("default").getAsString();

            model.defaultTexture = new ResourceLocation(type);
        }

        return model;
    }

    /**
     * Serializes {@link Model}
     * 
     * This method is responsible for cleaning up some of Model's fields (to 
     * make the file output more cleaner).
     */
    @Override
    public JsonElement serialize(Model src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonElement serial = plainGSON.toJsonTree(src, typeOfSrc);
        JsonObject map = serial.getAsJsonObject();

        map.remove("model");
        map.remove("defaultTexture");
        map.remove("scale");
        map.remove("scaleGui");
        map.remove("limbs");
        map.remove("poses");
        map.remove("providesObj");
        map.remove("providesMtl");

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

        if (src.scaleGui != 1)
        {
            map.addProperty("scaleGui", src.scaleGui);
        }

        if (src.providesObj)
        {
            map.addProperty("providesObj", src.providesObj);
        }

        if (src.providesMtl)
        {
            map.addProperty("providesMtl", src.providesMtl);
        }

        map.add("limbs", context.serialize(src.limbs));
        map.add("poses", context.serialize(src.poses));

        return map;
    }
}